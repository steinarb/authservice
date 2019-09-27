/*
 * Copyright 2018-2019 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class AuthserviceLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        Connection connection = createConnection();
        AuthserviceLiquibase handleregLiquibase = new AuthserviceLiquibase();
        handleregLiquibase.createInitialSchema(connection);
        handleregLiquibase.updateSchema(connection);
        String username = "jad";
        String password = "1ad";
        String salt = "pepper";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        addUser(connection, username, password, salt, email, firstname, lastname);
        assertUser(connection, username, password, salt);
        String rolename = "admin";
        addRole(connection, rolename, "Test role");
        addUserRole(connection, rolename, username);
        assertUserRole(connection, rolename, username);
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addUserRole(connection, "notarole", username);
            });
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addUserRole(connection, rolename, "notauser");
            });
        String permission = "user_admin_api_write";
        addPermission(connection, permission, "User admin REST API write access");
        addRolePermission(connection, rolename, permission);
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addRolePermission(connection, "notarole", permission);
            });
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
                addRolePermission(connection, rolename, "notapermission");
            });
    }

    @Test
    void testForceReleaseLocks() throws Exception {
        LogService logservice = new MockLogService();
        Connection connection = createConnection();
        AuthserviceLiquibase handleregLiquibase = new AuthserviceLiquibase();
        boolean success = handleregLiquibase.forceReleaseLocks(connection, logservice);
        assertTrue(success);
    }

    @Test
    void testForceReleaseLocksWithFailure() throws Exception {
        LogService logservice = new MockLogService();
        Connection connection = mock(Connection.class);
        AuthserviceLiquibase handleregLiquibase = new AuthserviceLiquibase();
        boolean success = handleregLiquibase.forceReleaseLocks(connection, logservice);
        assertFalse(success);
    }

    private void addUser(Connection connection, String username, String password, String salt, String email, String firstname, String lastname) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into users (username, password, password_salt, email, firstname, lastname) values (?, ?, ?, ?, ?, ?)")) {
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
        try (PreparedStatement statement = connection.prepareStatement("insert into roles (role_name, description) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    private void addUserRole(Connection connection, String rolename, String username) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("insert into user_roles (role_name, username) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    private void assertUser(Connection connection, String username, String password, String salt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select * from users where username=?")) {
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            assertTrue(results.next(), "Expected at least one match");
        }
    }

    private void assertUserRole(Connection connection, String rolename, String username) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("select * from user_roles where role_name=? and username=?")) {
            statement.setString(1, rolename);
            statement.setString(2, username);
            ResultSet results = statement.executeQuery();
            assertTrue(results.next(), "Expected at least one match");
        }
    }

    private void addPermission(Connection connection, String permission, String description) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("insert into permissions (permission_name, description) values (?, ?)")) {
            statement.setString(1, permission);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    private void addRolePermission(Connection connection, String rolename, String permission) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("insert into roles_permissions (role_name, permission_name) values (?, ?)")) {
            statement.setString(1, rolename);
            statement.setString(2, permission);
            statement.executeUpdate();
        }
    }

    private Connection createConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        DataSource dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

}
