/*
 * Copyright 2019-2025 Steinar Bang
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
package no.priv.bang.authservice.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import static org.assertj.core.api.Assertions.*;

import no.priv.bang.authservice.db.liquibase.test.TestLiquibaseRunner;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.authservice.web.security.dbrealm.AuthserviceDbRealm;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementConfig;
import no.priv.bang.osgiservice.users.UserRoles;

class UserManagementServiceProviderTest {
    private static DataSource datasource;

    @BeforeAll
    static void setupForAll() throws Exception {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        var runner = new TestLiquibaseRunner();
        runner.activate();
        runner.prepare(datasource);
    }

    @Test
    void testGetUser() {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var username = "jod";
        var user = provider.getUser(username);
        assertThat(user)
            .hasFieldOrProperty("userid")
            .hasFieldOrPropertyWithValue("username", username)
            .hasFieldOrPropertyWithValue("firstname", "John")
            .hasFieldOrPropertyWithValue("lastname", "Doe")
            .hasFieldOrPropertyWithValue("email", "johndoe6789@gmail.com")
            .hasFieldOrPropertyWithValue("numberOfFailedLogins", 0)
            .hasFieldOrPropertyWithValue("isLocked", false);
    }

    @Test
    void testGetUserWithEmptyResults() throws Exception {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var username = "jod";
        assertThrows(AuthserviceException.class, () -> provider.getUser(username));
    }

    @Test
    void testGetUserWhenSQLExceptionIsThrown() throws Exception {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var username = "jod";
        assertThrows(AuthserviceException.class, () -> provider.getUser(username));
    }

    @Test
    void testGetRolesForUser() {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var username = "jod";
        var roles = provider.getRolesForUser(username);
        assertThat(roles).isNotEmpty();
    }

    @Test
    void testGetRolesForUserWhenSqlExceptionIsThrown() throws Exception {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        when(mockdatasource.getConnection()).thenThrow(AuthserviceException.class);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var username = "jod";
        assertThrows(AuthserviceException.class, () -> provider.getRolesForUser(username));
    }

    @Test
    void testGetPermissionsForUser() {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var username = "jod";
        var permissions = provider.getPermissionsForUser(username);
        assertThat(permissions).isNotEmpty();
    }

    @Test
    void testGetPermissionsForUserWhenSQLExceptionIsThrown() throws Exception {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        when(mockdatasource.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var username = "jod";
        assertThrows(AuthserviceException.class, () -> provider.getPermissionsForUser(username));
    }

    @Test
    void testListUsersWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getUsers);
    }

    @Test
    void testModifyUserWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var dummy = User.with().build();
        assertThrows(AuthserviceException.class, () -> provider.modifyUser(dummy));
    }

    @Test
    void testModifyUser() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var users = provider.getUsers();
        assertThat(users).isNotEmpty();
        var firstUser = users.get(0);
        var modifiedUser = User.with(firstUser).firstname("John").lastname("Smith").build();
        var updatedUsers = provider.modifyUser(modifiedUser);
        var updatedUser = updatedUsers.get(0);
        assertEquals(modifiedUser.firstname(), updatedUser.firstname());
        assertEquals(modifiedUser.lastname(), updatedUser.lastname());
    }

    @Test
    void testUpdatePassword() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var users = provider.getUsers();
        var user = users.get(0);
        var newPassword = "zecret";
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1(newPassword)
            .password2(newPassword)
            .passwordsNotIdentical(false)
            .build();
        var updatedUsers = provider.updatePassword(passwords);
        assertEquals(users.size(), updatedUsers.size());

        // Check that the new password works
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        realm.activate();
        var token = new UsernamePasswordToken(user.username(), newPassword.toCharArray());
        var authenticationInfoForUser = realm.getAuthenticationInfo(token);
        assertEquals(1, authenticationInfoForUser.getPrincipals().asList().size());
    }

    @Test
    void testUpdatePasswordDifferentPasswords() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var users = provider.getUsers();
        var user = users.get(0);
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("not")
            .password2("matching")
            .passwordsNotIdentical(false)
            .build();
        assertThrows(AuthservicePasswordsNotIdenticalException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordNullPasswords() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var users = provider.getUsers();
        var user = users.get(0);
        var passwords = UserAndPasswords.with().user(user).build();
        assertThrows(AuthservicePasswordEmptyException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordEmptyPassword() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var users = provider.getUsers();
        var user = users.get(0);
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("")
            .password2(null)
            .build();
        assertThrows(AuthservicePasswordEmptyException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordNullUser() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var passwords = UserAndPasswords.with()
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(AuthservicePasswordEmptyException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordWithWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var user = User.with()
            .userid(100)
            .username("notmatching")
            .email("nomatch@gmail.com")
            .firstname("Not")
            .lastname("Match")
            .build();
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(AuthserviceException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordUserNotInDatabase() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var user = User.with()
            .userid(100)
            .username("notmatching")
            .email("nomatch@gmail.com")
            .firstname("Not")
            .lastname("Match")
            .build();
        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(AuthserviceException.class, () -> provider.updatePassword(passwords));
    }

    @Test
    void testAddUser() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var usersBeforeAddingOne = provider.getUsers();
        var newUser = User.with()
            .userid(-1)
            .username("jsmith")
            .email("johnsmith31@gmail.com")
            .firstname("John")
            .lastname("Smith")
            .build();
        var newUserPassword = "supersecret";
        var newUserWithPasswords = UserAndPasswords.with()
            .user(newUser)
            .password1(newUserPassword)
            .password2(newUserPassword)
            .build();
        var users = provider.addUser(newUserWithPasswords);
        assertThat(users).hasSizeGreaterThan(usersBeforeAddingOne.size());

        // Check that the password of the new user is as expected
        var user = users.stream().filter(u -> "jsmith".equals(u.username())).findFirst().get();
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        realm.activate();
        var token = new UsernamePasswordToken(user.username(), newUserPassword.toCharArray());
        var authenticationInfoForUser = realm.getAuthenticationInfo(token);
        assertEquals(1, authenticationInfoForUser.getPrincipals().asList().size());
    }

    @Test
    void testAddUserWhenUsernameExists() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var newUser = User.with()
            .userid(-1)
            .username("admin")
            .email("admin@gmail.com")
            .firstname("Admin")
            .lastname("Istrator")
            .build();
        var newUserPassword = "supersecret";
        var newUserWithPasswords = UserAndPasswords.with()
            .user(newUser)
            .password1(newUserPassword)
            .password2(newUserPassword)
            .build();
        assertThrows(AuthserviceException.class, () -> provider.addUser(newUserWithPasswords));
    }

    @Test
    void testAddUserWhenNewUserCantBeRetrievedFromTheBase() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        var newUser = User.with()
            .userid(-1)
            .username("jod")
            .email("jod31@gmail.com")
            .firstname("John")
            .lastname("Doe")
            .build();
        var newUserPassword = "supersecret";
        var newUserWithPasswords = UserAndPasswords.with()
            .user(newUser)
            .password1(newUserPassword)
            .password2(newUserPassword)
            .build();
        assertThrows(AuthserviceException.class, () -> provider.addUser(newUserWithPasswords));
    }

    @Test
    void testUnlockUser() {
        var provider = new UserManagementServiceProvider();
        var logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var username = "lu";
        var lockedUser = provider.getUser(username);
        assertThat(lockedUser.isLocked()).isTrue();
        assertThat(lockedUser.numberOfFailedLogins()).isEqualTo(3);

        // Unlock the user
        var usersWithUnlockedUser = provider.unlockUser(username);
        var unlockedUser = usersWithUnlockedUser.stream().filter(u -> u.username().equals(username)).findFirst().get();
        assertThat(unlockedUser.isLocked()).isFalse();
        assertThat(unlockedUser.numberOfFailedLogins()).isZero();
    }

    @Test
    void testUnlockUserWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> provider.unlockUser("jad"));
    }

    @Test
        void testAddAndModifyRoles() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var originalRoles = provider.getRoles();
        assertThat(originalRoles).isNotEmpty();

        var dummy1 = Role.with().id(-1).rolename("dummy").description("dummy").build();
        var rolesAfterAdd = provider.addRole(dummy1);
        assertThat(rolesAfterAdd).hasSizeGreaterThan(originalRoles.size());

        // Verify that trying to insert a new role with the same rolename fails
        var dummy2 = Role.with().id(-1).rolename("dummy").description("dummy").build();
        assertThrows(AuthserviceException.class, () -> provider.addRole(dummy2));

        // Modify a role
        var roleToModify = rolesAfterAdd.stream().filter(r -> "dummy".equals(r.rolename())).findFirst().get();
        var modifiedRoleToUpdate = Role.with(roleToModify)
            .rolename("dumy")
            .description("A new description")
            .build();
        var modifiedRoles = provider.modifyRole(modifiedRoleToUpdate);
        var modifiedRole = modifiedRoles.stream().filter(r -> roleToModify.id() == r.id()).findFirst().get();
        assertEquals(modifiedRoleToUpdate.rolename(), modifiedRole.rolename());
        assertEquals(modifiedRoleToUpdate.description(), modifiedRole.description());

        // Verify that changing the role name to a name already in the database fails
        var failingToUpdate = Role.with(modifiedRole)
            .rolename("admin")
            .build();
        assertThrows(AuthserviceException.class, () -> provider.modifyRole(failingToUpdate));

        // Verify that trying to modify a role not present logs a warning
        // (use an id not found in the database)
        var numberOfLogMessagesBeforeUpdate = logservice.getLogmessages().size();
        var failingToUpdate2 = Role.with(modifiedRole)
            .id(1000)
            .rolename("dummy")
            .build();
        var failedToUpdateRoles2 = provider.modifyRole(failingToUpdate2);
        assertEquals(modifiedRoles.size(), failedToUpdateRoles2.size());
        var numberOfLogMessagesAfterUpdate = logservice.getLogmessages().size();
        assertThat(numberOfLogMessagesAfterUpdate).isGreaterThan(numberOfLogMessagesBeforeUpdate);
    }

    @Test
    void testGetRolesWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getRoles);
    }

    @Test
    void testAddAndModifyPermissions() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var originalPermissions = provider.getPermissions();
        assertThat(originalPermissions).isNotEmpty();

        var dummy1 = Permission.with().id(-1).permissionname("dummy").description("dummy").build();
        var permissionsAfterAdd = provider.addPermission(dummy1);
        assertThat(permissionsAfterAdd).hasSizeGreaterThan(originalPermissions.size());

        // Verify that trying to insert a new role with the same permissionname fails
        var dummy2 = Permission.with().id(-1).permissionname("dummy").description("dummy").build();
        assertThrows(AuthserviceException.class, () -> provider.addPermission(dummy2));

        // Modify a permission
        var permissionToModify = permissionsAfterAdd.stream().filter(r -> "dummy".equals(r.permissionname())).findFirst().get();
        var modifiedPermissionToUpdate = Permission.with(permissionToModify)
            .permissionname("dumy")
            .description("A new description")
            .build();
        var modifiedPermissions = provider.modifyPermission(modifiedPermissionToUpdate);
        var modifiedPermission = modifiedPermissions.stream().filter(r -> permissionToModify.id() == r.id()).findFirst().get();
        assertEquals(modifiedPermissionToUpdate.permissionname(), modifiedPermission.permissionname());
        assertEquals(modifiedPermissionToUpdate.description(), modifiedPermission.description());

        // Verify that changing the permission name to a name already in the database fails
        var failingToUpdate = Permission.with(modifiedPermission)
            .permissionname("user_admin_api_read")
            .build();
        assertThrows(AuthserviceException.class, () -> provider.modifyPermission(failingToUpdate));

        // Verify that trying to modify a permission not present logs a warning
        // (use an id not found in the database)
        var numberOfLogMessagesBeforeUpdate = logservice.getLogmessages().size();
        var failingToUpdate2 = Permission.with(modifiedPermission)
            .id(1000)
            .permissionname("dummy")
            .build();
        var failedToUpdatePermission2 = provider.modifyPermission(failingToUpdate2);
        assertEquals(modifiedPermissions.size(), failedToUpdatePermission2.size());
        var numberOfLogMessagesAfterUpdate = logservice.getLogmessages().size();
        assertThat(numberOfLogMessagesAfterUpdate).isGreaterThan(numberOfLogMessagesBeforeUpdate);
    }

    @Test
    void testGetPermissionsWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getPermissions);
    }

    @Test
    void testAddAndModifyUserRoles() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var originalUserRoles = provider.getUserRoles();
        assertThat(originalUserRoles).isNotEmpty();

        // Add a new user role
        var user = provider.getUsers().get(0);
        var newRole = provider.getRoles().stream().filter(r -> "visitor".equals(r.rolename())).findFirst().get();
        var originalRolesForUser = originalUserRoles.get(user.username());
        var userroles = UserRoles.with().user(user).roles(Arrays.asList(newRole)).build();
        var userRolesAfterAddingRole = provider.addUserRoles(userroles );
        assertThat(userRolesAfterAddingRole.get(user.username())).hasSizeGreaterThan(originalRolesForUser.size());

        // Add the same role again and verify that the role count doesn't increase
        var userRolesAfterAddingRole2 = provider.addUserRoles(userroles);
        assertEquals(userRolesAfterAddingRole.get(user.username()).size(), userRolesAfterAddingRole2.get(user.username()).size());

        // Try adding a non-existing role
        var nonExistingRole = Role.with().id(42).rolename("notfound").description("dummy").build();
        var userroles2 = UserRoles.with().user(user).roles(Arrays.asList(nonExistingRole)).build();
        assertThrows(AuthserviceException.class, () -> provider.addUserRoles(userroles2));

        // Remove the role
        var userRolesAfterRemovingRole = provider.removeUserRoles(UserRoles.with().user(user).roles(Arrays.asList(newRole)).build());
        assertThat(userRolesAfterRemovingRole.get(user.username())).hasSizeLessThan(userRolesAfterAddingRole.get(user.username()).size());

        // Try removing the role again and observe that the count is the same
        var userRolesAfterRemovingRole2 = provider.removeUserRoles(UserRoles.with().user(user).roles(Arrays.asList(newRole)).build());
        assertEquals(userRolesAfterRemovingRole.get(user.username()).size(), userRolesAfterRemovingRole2.get(user.username()).size());

        // Try removing an empty role list and observe that the count is the same
        var userRolesAfterRemovingRole3 = provider.removeUserRoles(UserRoles.with().user(user).roles(Collections.emptyList()).build());
        assertEquals(userRolesAfterRemovingRole.get(user.username()).size(), userRolesAfterRemovingRole3.get(user.username()).size());

        // Try removing a non-existing role and observe that the count is the same
        var userRolesAfterRemovingRole4 = provider.removeUserRoles(UserRoles.with().user(user).roles(Arrays.asList(nonExistingRole)).build());
        assertEquals(userRolesAfterRemovingRole.get(user.username()).size(), userRolesAfterRemovingRole4.get(user.username()).size());
    }

    @Test
    void testUserRolesOperationsWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getUserRoles);

        var user = User.with().build();
        assertThrows(AuthserviceException.class, () -> provider.findExistingRolesForUser(user));

        var userroles = UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build();
        assertThrows(AuthserviceException.class, () -> provider.removeUserRoles(userroles));
    }

    @Test
    void testAddAndModifyRolesPermissions() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var originalRolesPermissions = provider.getRolesPermissions();
        assertThat(originalRolesPermissions).isNotEmpty();
        var firstRoleName = originalRolesPermissions.entrySet().stream().findFirst().get().getKey();
        var firstRole = provider.getRoles().stream().filter(r -> r.rolename().equals(firstRoleName)).findFirst().get();
        var firstPermission = originalRolesPermissions.get(firstRoleName).stream().findFirst().get();
        assertNotEquals(firstRole.description(), firstPermission.description());

        // Add a new role permission
        var role = provider.getRoles().get(1);
        var newPermission = provider.getPermissions().stream().filter(r -> "user_read".equals(r.permissionname())).findFirst().get();
        var originalRolesForUser = originalRolesPermissions.get(role.rolename());
        var rolesPermissionsAfterAddingRole = provider.addRolePermissions(RolePermissions.with().role(role).permissions(Arrays.asList(newPermission)).build());
        assertThat(rolesPermissionsAfterAddingRole.get(role.rolename())).hasSizeGreaterThan(originalRolesForUser.size());

        // Add the same permission again and verify that the role count doesn't increase
        var rolesPermissionsAfterAddingRole2 = provider.addRolePermissions(RolePermissions.with().role(role).permissions(Arrays.asList(newPermission)).build());
        assertEquals(rolesPermissionsAfterAddingRole.get(role.rolename()).size(), rolesPermissionsAfterAddingRole2.get(role.rolename()).size());

        // Try adding a non-existing role
        var nonExistingPermission = Permission.with().id(42).permissionname("notfound").description("dummy").build();
        var rolepermissions = RolePermissions.with().role(role).permissions(Arrays.asList(nonExistingPermission)).build();
        assertThrows(AuthserviceException.class, () -> provider.addRolePermissions(rolepermissions));

        // Remove the role
        var rolesPermissionsAfterRemovingRole = provider.removeRolePermissions(RolePermissions.with().role(role).permissions(Arrays.asList(newPermission)).build());
        assertThat(rolesPermissionsAfterRemovingRole.get(role.rolename())).hasSizeLessThan(rolesPermissionsAfterAddingRole.get(role.rolename()).size());

        // Try removing the role again and observe that the count is the same
        var rolesPermissionsAfterRemovingRole2 = provider.removeRolePermissions(RolePermissions.with().role(role).permissions(Arrays.asList(newPermission)).build());
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.rolename()).size(), rolesPermissionsAfterRemovingRole2.get(role.rolename()).size());

        // Try removing an empty role list and observe that the count is the same
        var rolesPermissionsAfterRemovingRole3 = provider.removeRolePermissions(RolePermissions.with().role(role).permissions(Collections.emptyList()).build());
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.rolename()).size(), rolesPermissionsAfterRemovingRole3.get(role.rolename()).size());

        // Try removing a non-existing role and observe that the count is the same
        var rolesPermissionsAfterRemovingRole4 = provider.removeRolePermissions(RolePermissions.with().role(role).permissions(Arrays.asList(nonExistingPermission)).build());
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.rolename()).size(), rolesPermissionsAfterRemovingRole4.get(role.rolename()).size());
    }

    @Test
    void testRolesPermissionsOperationsWhenSQLExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        var mockdatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getRolesPermissions);

        var role = Role.with().build();
        assertThrows(AuthserviceException.class, () -> provider.findExistingPermissionsForRole(role));

        var rolepermissions = RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build();
        assertThrows(AuthserviceException.class, () -> provider.removeRolePermissions(rolepermissions));
    }

    @Test
    void testGetAndSetConfig() {
        var logservice = new MockLogService();
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var config = provider.getConfig();
        var configToSave = UserManagementConfig.with().excessiveFailedLoginLimit(config.excessiveFailedLoginLimit() +1).build();
        var updatedConfig = provider.modifyConfig(configToSave);
        assertThat(updatedConfig.excessiveFailedLoginLimit()).isGreaterThan(config.excessiveFailedLoginLimit());
        provider.modifyConfig(config);
    }

    @Test
    void testGetConfigWithSQLExceptionThrown() throws Exception {
        var logservice = new MockLogService();
        var mockedDatasource = mock(DataSource.class);
        when(mockedDatasource.getConnection()).thenThrow(SQLException.class);
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(mockedDatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, provider::getConfig);
    }

    @Test
    void testGetConfigWithEmptyResults() throws Exception {
        var logservice = new MockLogService();
        var mockedDatasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var preparedStatement = mock(PreparedStatement.class);
        var statement = mock(Statement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery(anyString())).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);
        when(mockedDatasource.getConnection()).thenReturn(connection);
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(mockedDatasource);
        provider.activate();

        var emptyConfig = provider.getConfig();
        assertThat(emptyConfig.excessiveFailedLoginLimit()).isZero();
    }

    @Test
    void testSetConfigWithSQLExceptionThrown() throws Exception {
        var logservice = new MockLogService();
        var mockedDatasource = mock(DataSource.class);
        when(mockedDatasource.getConnection()).thenThrow(SQLException.class);
        var provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(mockedDatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> provider.modifyConfig(null));
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        var credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}
