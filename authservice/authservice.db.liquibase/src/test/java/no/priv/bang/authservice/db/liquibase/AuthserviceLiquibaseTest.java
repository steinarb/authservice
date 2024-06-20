/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.authservice.db.liquibase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;
import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

import liquibase.exception.CommandExecutionException;

class AuthserviceLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @BeforeAll
    static void initialSetup() throws Exception {
        try (var lpf = AuthserviceLiquibaseTest.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(lpf);
        }
    }

    @Test
    void testCreateSchema() throws Exception {
        var authserviceLiquibase = new AuthserviceLiquibase();

        authserviceLiquibase.createInitialSchema(createConnection("authservice"));

        try(var connection = createConnection("authservice")) {
            var username = "jad";
            var password = "1ad";
            var salt = "pepper";
            var email = "jad@gmail.com";
            var firstname = "Jane";
            var lastname = "Doe";
            addUser(connection, username, password, salt, email, firstname, lastname);
            assertUser(connection, username);
            var rolename = "admin";
            addRole(connection, rolename, "Test role");
            addUserRole(connection, rolename, username);
            assertUserRole(connection, rolename, username);
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addUserRole(connection, "notarole", username);
            });
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addUserRole(connection, rolename, "notauser");
            });
            var permission = "user_admin_api_write";
            addPermission(connection, permission, "User admin REST API write access");
            addRolePermission(connection, rolename, permission);
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addRolePermission(connection, "notarole", permission);
            });
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addRolePermission(connection, rolename, "notapermission");
            });
        }

        authserviceLiquibase.updateSchema(createConnection("authservice"));
    }

    @Test
    void testApplyChangelist() throws Exception {
        var authserviceLiquibase = new AuthserviceLiquibase();

        authserviceLiquibase.createInitialSchema(createConnection("authservice2"));
        authserviceLiquibase.applyLiquibaseChangelist(
            createConnection("authservice2"),
            "test-db-changelog/db-changelog.xml",
            getClass().getClassLoader());


        try(var connection = createConnection("authservice2")) {
            var username = "jad";
            assertUser(connection, username);
            var rolename = "caseworker";
            assertUserRole(connection, rolename, username);
        }
    }

    @Test
    void testApplyChangelistFailingWhenResourceIsMissing() throws Exception {
        var authserviceLiquibase = new AuthserviceLiquibase();
        var testClassLoader = getClass().getClassLoader();
        try (var conn1 = createConnection("authservice4")) {
            authserviceLiquibase.createInitialSchema(conn1);
        }

        var connection = createConnection("authservice4");
        var ex = assertThrows(
            CommandExecutionException.class,
            () -> authserviceLiquibase.applyLiquibaseChangelist(
                connection,
                "test-db-changelog/db-changelog-not-present.xml",
                testClassLoader));
        assertThat(ex.getMessage()).contains("The file test-db-changelog/db-changelog-not-present.xml was not found in the configured search path");
    }

    private void addUser(Connection connection, String username, String password, String salt, String email, String firstname, String lastname) throws SQLException {
        try (var statement = connection.prepareStatement("insert into users (username, password, password_salt, email, firstname, lastname) values (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, salt);
            statement.setString(4, email);
            statement.setString(5, firstname);
            statement.setString(6, lastname);
            statement.executeUpdate();
        }
    }

    private void addRole(Connection connection, String rolename, String description) throws Exception {
        try (var statement = connection.prepareStatement("insert into roles (role_name, description) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    private void addUserRole(Connection connection, String rolename, String username) throws Exception {
        try (var statement = connection.prepareStatement("insert into user_roles (role_name, username) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    private void assertUser(Connection connection, String username) throws SQLException {
        try (var statement = connection.prepareStatement("select * from users where username=?")) {
            statement.setString(1, username);
            var results = statement.executeQuery();
            assertTrue(results.next(), "Expected at least one match");
        }
    }

    private void assertUserRole(Connection connection, String rolename, String username) throws Exception {
        try (var statement = connection.prepareStatement("select * from user_roles where role_name=? and username=?")) {
            statement.setString(1, rolename);
            statement.setString(2, username);
            var results = statement.executeQuery();
            assertTrue(results.next(), "Expected at least one match");
        }
    }

    private void addPermission(Connection connection, String permission, String description) throws Exception {
        try (var statement = connection.prepareStatement("insert into permissions (permission_name, description) values (?, ?)")) {
            statement.setString(1, permission);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    private void addRolePermission(Connection connection, String rolename, String permission) throws Exception {
        try (var statement = connection.prepareStatement("insert into roles_permissions (role_name, permission_name) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, permission);
            statement.executeUpdate();
        }
    }

    private Connection createConnection(String dbname) throws Exception {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

    Connection createMockConnection() throws Exception {
        var connection = new MockConnection();
        var metadata = mock(DatabaseMetaData.class);
        when(metadata.getDatabaseProductName()).thenReturn("mockdb");
        when(metadata.getSQLKeywords()).thenReturn("insert, select, delete");
        when(metadata.getURL()).thenReturn("jdbc:mock:///authservice");
        var tables = new MockResultSet("tables");
        when(metadata.getTables(anyString(), anyString(), anyString(), any(String[].class))).thenReturn(tables);
        connection.setMetaData(metadata);
        return connection;
    }

}
