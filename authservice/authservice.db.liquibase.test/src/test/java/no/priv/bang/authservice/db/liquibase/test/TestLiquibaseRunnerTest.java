/*
 * Copyright 2018-2022 Steinar Bang
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
package no.priv.bang.authservice.db.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.definitions.AuthserviceException;

class TestLiquibaseRunnerTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.activate();
        DataSource datasource = createDatasource();
        runner.prepare(datasource);

        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statment = connection.prepareStatement("select * from users")) {
                ResultSet results = statment.executeQuery();
                int usercount = 0;
                while(results.next()) {
                    ++usercount;
                }

                assertEquals(5, usercount);
            }
        }

        try(Connection connection = datasource.getConnection()) {
            try(PreparedStatement statment = connection.prepareStatement("select * from roles")) {
                ResultSet results = statment.executeQuery();
                int rolecount = 0;
                while(results.next()) {
                    ++rolecount;
                }

                assertEquals(4, rolecount);
            }
        }
    }

    @Test
    void testCreateSchemaWhenSQLExceptionIsThrown() throws Exception {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        runner.activate();
        AuthserviceException ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to create schema for authservice Derby test database component");
    }

    @Test
    void testFailWhenInsertingMockData() throws Exception {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        DataSource realdb = createDatasource();
        DataSource datasource = spy(realdb);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenThrow(SQLException.class);

        runner.activate();
        AuthserviceException ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to insert mock data in authservice Derby test database component");
    }

    @Test
    void testFailWhenUpdatingSchema() throws Exception {
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        DataSource realdb = createDatasource();
        DataSource datasource = spy(realdb);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenThrow(SQLException.class);

        runner.activate();
        AuthserviceException ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to update schema of authservice Derby test database component");
    }

    private DataSource createDatasource() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:authservice;create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
