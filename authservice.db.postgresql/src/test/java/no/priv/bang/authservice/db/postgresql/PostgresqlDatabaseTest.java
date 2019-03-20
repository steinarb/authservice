/*
 * Copyright 2019 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.authservice.db.postgresql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import static no.priv.bang.authservice.definitions.AuthserviceConstants.*;

import no.priv.bang.authservice.db.postgresql.PostgresqlDatabase;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class PostgresqlDatabaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreate() throws Exception {
        MockLogService logservice = new MockLogService();
        PostgresqlDatabase database = new PostgresqlDatabase();
        database.setLogservice(logservice);
        database.setDataSourceFactory(derbyDataSourceFactory);
        Map<String, Object> config = createConfigThatWillWorkWithDerby();
        database.activate(config);

        try(Connection connection = database.getConnection()) {
            try(PreparedStatement statment = connection.prepareStatement("select * from users")) {
                ResultSet results = statment.executeQuery();
                int usercount = 0;
                while(results.next()) {
                    ++usercount;
                }

                assertEquals(1, usercount);
            }
        }

        try(Connection connection = database.getDatasource().getConnection()) {
            try(PreparedStatement statment = connection.prepareStatement("select * from roles")) {
                ResultSet results = statment.executeQuery();
                int rolecount = 0;
                while(results.next()) {
                    ++rolecount;
                }

                assertEquals(1, rolecount);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCreateWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        PostgresqlDatabase database = new PostgresqlDatabase();
        database.setLogservice(logservice);
        DataSourceFactory factory = mock(DataSourceFactory.class);
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        when(factory.createDataSource(any())).thenReturn(datasource);
        database.setDataSourceFactory(factory);

        assertThrows(AuthserviceException.class, () -> {
                database.activate(Collections.emptyMap());
            });
    }

    @Test
    public void testCreateDatabaseConnectionProperties() {
        Map<String, Object> config = new HashMap<>();
        config.put(AUTHSERVICE_JDBC_URL, "jdbc:postgresql:///ukelonn");
        config.put(AUTHSERVICE_JDBC_USER, "authservice");
        config.put(AUTHSERVICE_JDBC_PASSWORD, "secret");
        Properties properties = PostgresqlDatabase.createDatabaseConnectionProperties(config);
        assertEquals("jdbc:postgresql:///ukelonn", properties.getProperty(DataSourceFactory.JDBC_URL));
        assertEquals("authservice", properties.getProperty(DataSourceFactory.JDBC_USER));
        assertEquals("secret", properties.getProperty(DataSourceFactory.JDBC_PASSWORD));
    }

    @Test
    public void testCreateDatabaseConnectionPropertiesWithEmptyConfigValues() {
        Map<String, Object> config = new HashMap<>();
        config.put(AUTHSERVICE_JDBC_URL, "");
        config.put(AUTHSERVICE_JDBC_USER, "");
        config.put(AUTHSERVICE_JDBC_PASSWORD, "");
        Properties properties = PostgresqlDatabase.createDatabaseConnectionProperties(config);
        assertEquals("", properties.getProperty(DataSourceFactory.JDBC_URL));
        // Verify that empty username and password can be used to remove username and password from the properties
        assertNull(properties.getProperty(DataSourceFactory.JDBC_USER));
        assertNull(properties.getProperty(DataSourceFactory.JDBC_PASSWORD));
    }

    @Test
    public void testCreateDatabaseConnectionPropertiesDefaultsOnEmptyConfig() {
        Properties properties = PostgresqlDatabase.createDatabaseConnectionProperties(Collections.emptyMap());
        assertEquals("jdbc:postgresql:///authservice", properties.getProperty(DataSourceFactory.JDBC_URL));
        assertEquals("karaf", properties.getProperty(DataSourceFactory.JDBC_USER));
        assertEquals("karaf", properties.getProperty(DataSourceFactory.JDBC_PASSWORD));
    }

    private Map<String, Object> createConfigThatWillWorkWithDerby() {
        Map<String, Object> config = new HashMap<>();
        config.put(AUTHSERVICE_JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        return config;
    }

}
