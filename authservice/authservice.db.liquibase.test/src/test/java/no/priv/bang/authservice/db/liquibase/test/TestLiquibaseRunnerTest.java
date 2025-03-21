/*
 * Copyright 2018-2025 Steinar Bang
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
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnectionFactory;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.definitions.AuthserviceException;

class TestLiquibaseRunnerTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        var runner = new TestLiquibaseRunner();
        runner.activate();
        var datasource = createDatasource();
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();
        runner.prepare(datasource);

        var usersTable = assertjConnection.table("users").build();
        assertThat(usersTable).exists().hasNumberOfRows(6);
        var rolesTable = assertjConnection.table("roles").build();
        assertThat(rolesTable).exists().hasNumberOfRows(4);
    }

    @Test
    void testCreateSchemaWhenSQLExceptionIsThrown() throws Exception {
        var runner = new TestLiquibaseRunner();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to create schema for authservice Derby test database component");
    }

    @Test
    void testFailWhenInsertingMockData() throws Exception {
        var runner = new TestLiquibaseRunner();
        var realdb = createDatasource();
        var datasource = spy(realdb);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenThrow(SQLException.class);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to insert mock data in authservice Derby test database component");
    }

    @Test
    void testFailWhenUpdatingSchema() throws Exception {
        var runner = new TestLiquibaseRunner();
        var realdb = createDatasource();
        var datasource = spy(realdb);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenThrow(SQLException.class);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to update schema of authservice Derby test database component");
    }

    @Test
    void testFailWhenInsertingMockDataForLockedUser() throws Exception {
        var runner = new TestLiquibaseRunner();
        var realdb = createDatasource();
        var datasource = spy(realdb);
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenThrow(SQLException.class);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to insert mock data for locked user in authservice Derby test database component");
    }

    private DataSource createDatasource() throws SQLException {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:authservice;create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
