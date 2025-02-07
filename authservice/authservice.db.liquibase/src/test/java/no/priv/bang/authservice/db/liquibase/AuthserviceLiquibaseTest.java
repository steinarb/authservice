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
package no.priv.bang.authservice.db.liquibase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
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

import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnectionFactory;
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
        var datasource = createDataSource("authservice");
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();

        authserviceLiquibase.createInitialSchema(datasource.getConnection());

        // Check existence of created tables with user admin with role useradmin in place and empty permissions
        var initialUsersTable = assertjConnection.table("users").build();
        assertThat(initialUsersTable).exists().hasNumberOfRows(1).column("username").value().isEqualTo("admin");
        var initalRolesTable = assertjConnection.table("roles").build();
        assertThat(initalRolesTable).exists().hasNumberOfRows(1).column("role_name").value().isEqualTo("useradmin");
        var initialUserrolesTable = assertjConnection.table("user_roles").build();
        assertThat(initialUserrolesTable).exists().hasNumberOfRows(1)
            .column("username").value().isEqualTo("admin")
            .column("role_name").value().isEqualTo("useradmin");
        var initalPermissionsTable = assertjConnection.table("permissions").build();
        assertThat(initalPermissionsTable).exists().isEmpty();
        var initalRolepermissionsTable = assertjConnection.table("roles_permissions").build();
        assertThat(initalRolepermissionsTable).exists().isEmpty();

        // Add user
        var username = "jad";
        var password = "1ad";
        var salt = "pepper";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        try(var connection = createConnection("authservice")) {
            addUser(connection, username, password, salt, email, firstname, lastname);
        }
        var usersTableWithAddedUser = assertjConnection.table("users").build();
        assertThat(usersTableWithAddedUser).exists().hasNumberOfRows(2).column("username")
            .value().isEqualTo("admin")
            .value().isEqualTo(username);

        // Add role and set on user
        var rolename = "admin";
        try(var connection = createConnection("authservice")) {
            addRole(connection, rolename, "Test role");
            addUserRole(connection, rolename, username);
        }
        var rolesTableAfterAddingRole = assertjConnection.table("roles").build();
        assertThat(rolesTableAfterAddingRole).exists().hasNumberOfRows(2).column("role_name")
            .value().isEqualTo("useradmin")
            .value().isEqualTo(rolename);
        var userrolesTableAfterSettingRoleOnUser = assertjConnection.table("user_roles").build();
        assertThat(userrolesTableAfterSettingRoleOnUser).exists().hasNumberOfRows(2)
            .column("username").value().isEqualTo("admin").value().isEqualTo(username)
            .column("role_name").value().isEqualTo("useradmin").value().isEqualTo(rolename);

        // Verify database constraints on user and roles relation
        try(var connection = createConnection("authservice")) {
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> addUserRole(connection, "notarole", username));
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> addUserRole(connection, rolename, "notauser"));
        }

        // Add permission and connect to role
        var permission = "user_admin_api_write";
        try(var connection = createConnection("authservice")) {
            addPermission(connection, permission, "User admin REST API write access");
            addRolePermission(connection, rolename, permission);
        }
        var permissionsTableWithPermissionAdded = assertjConnection.table("permissions").build();
        assertThat(permissionsTableWithPermissionAdded).exists().hasNumberOfRows(1).column("permission_name").value().isEqualTo(permission);
        var rolepermissionsTableWithRoleConnectedToPermission = assertjConnection.table("roles_permissions").build();
        assertThat(rolepermissionsTableWithRoleConnectedToPermission).exists().hasNumberOfRows(1)
            .column("role_name").value().isEqualTo(rolename)
            .column("permission_name").value().isEqualTo(permission);

        // Verify database constraints on users and permissions relation
        try(var connection = createConnection("authservice")) {
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> addRolePermission(connection, "notarole", permission));
            assertThrows(SQLIntegrityConstraintViolationException.class, () -> addRolePermission(connection, rolename, "notapermission"));
        }

        authserviceLiquibase.updateSchema(datasource.getConnection());

        var usersTableAfterSchemaUpdate = assertjConnection.table("users").build();

        // Verify that columns have been added to users table with default value
        assertThat(usersTableAfterSchemaUpdate).exists().hasNumberOfRows(2)
            .column("failed_login_count").value().isEqualTo(0)
            .column("is_locked").value().isEqualTo(false);

        // Verify that config table is in place with its single row
        var configTable = assertjConnection.table("authservice_config").build();
        assertThat(configTable).exists().column("excessive_failed_login_limit").value().isEqualTo(3);
    }

    @Test
    void testApplyChangelist() throws Exception {
        var authserviceLiquibase = new AuthserviceLiquibase();
        var datasource = createDataSource("authservice2");
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();

        authserviceLiquibase.createInitialSchema(datasource.getConnection());
        authserviceLiquibase.applyLiquibaseChangelist(
            datasource.getConnection(),
            "test-db-changelog/db-changelog.xml",
            getClass().getClassLoader());


        var usersTable = assertjConnection.table("users").build();
        assertThat(usersTable).exists().hasNumberOfRows(5)
            .column("username").hasValues("admin", "on", "kn", "jad", "jod");
        var userrolesTable = assertjConnection.table("user_roles").build();
        assertThat(userrolesTable).exists().hasNumberOfRows(7)
            .column("username").containsValues("admin", "admin", "on", "kn", "on", "jad", "jod")
            .column("role_name").hasValues("useradmin", "admin", "admin", "admin", "caseworker", "caseworker", "caseworker");
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
        var dataSource = createDataSource(dbname);
        return dataSource.getConnection();
    }

    private DataSource createDataSource(String dbname) throws SQLException {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource;
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
