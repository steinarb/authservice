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
package no.priv.bang.authservice.db.liquibase.production;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ProductionLiquibaseRunnerTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        MockLogService logservice = new MockLogService();
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        runner.setLogservice(logservice);
        runner.activate();
        DataSource database = derbyDataSourceFactory.createDataSource(createConfigThatWillWorkWithDerby());
        runner.prepare(database);
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

        try(Connection connection = database.getConnection()) {
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
    void testCreateSchemaWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        runner.setLogservice(logservice);
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenReturn(connection);

        runner.activate();
        assertThrows(AuthserviceException.class, () -> {
                runner.prepare(datasource);
            });
        assertEquals(1, logservice.getLogmessages().size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCreateSchemaWhenLockExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        ProductionLiquibaseRunner runner = new ProductionLiquibaseRunner();
        runner.setLogservice(logservice);
        DataSourceFactory factory = mock(DataSourceFactory.class);
        DataSource datasource = mock(DataSource.class);
        Connection connection = createMockConnection();
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenReturn(connection);
        when(factory.createDataSource(any())).thenReturn(datasource);

        runner.activate();
        runner.prepare(datasource);
        assertEquals(2, logservice.getLogmessages().size());
    }

    Connection createMockConnection() throws Exception {
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        when(metadata.getDatabaseProductName()).thenReturn("mockdb");
        when(metadata.getSQLKeywords()).thenReturn("insert, select, delete");
        ResultSet tables = mock(ResultSet.class);
        when(metadata.getTables(anyString(), anyString(), anyString(), any(String[].class))).thenReturn(tables);
        Statement stmnt = mock(Statement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenReturn(true).thenReturn(false);
        when(stmnt.executeQuery(anyString())).thenReturn(results);
        when(stmnt.getUpdateCount()).thenReturn(-1);
        when(connection.createStatement()).thenReturn(stmnt);
        when(connection.getMetaData()).thenReturn(metadata);
        return connection;
    }

    private Properties createConfigThatWillWorkWithDerby() {
        Properties config = new Properties();
        config.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        return config;
    }

}
