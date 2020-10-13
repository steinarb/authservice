/*
 * Copyright 2019-2020 Steinar Bang
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
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
import no.priv.bang.osgiservice.users.UserRoles;

class UserManagementServiceProviderTest {
    private static DataSource datasource;

    @BeforeAll
    static void setupForAll() throws Exception {
        DerbyDataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        MockLogService logservice = new MockLogService();
        TestLiquibaseRunner runner = new TestLiquibaseRunner();
        runner.setLogservice(logservice);
        runner.activate();
        runner.prepare(datasource);
    }

    @Test
    void testGetUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        String username = "jod";
        User user = provider.getUser(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    void testGetUserWithEmptyResults() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        String username = "jod";
        assertThrows(AuthserviceException.class, () -> {
                provider.getUser(username);
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetUserWhenSQLExceptionIsThrown() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        String username = "jod";
        assertThrows(AuthserviceException.class, () -> {
                provider.getUser(username);
            });
    }

    @Test
    void testGetRolesForUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        String username = "jod";
        List<Role> roles = provider.getRolesForUser(username);
        assertThat(roles.size()).isPositive();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetRolesForUserWhenSqlExceptionIsThrown() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        when(mockdatasource.getConnection()).thenThrow(AuthserviceException.class);
        provider.setDataSource(mockdatasource);
        provider.activate();

        String username = "jod";
        assertThrows(AuthserviceException.class, () -> {
                provider.getRolesForUser(username);
            });
    }

    @Test
    void testGetPermissionsForUser() {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        String username = "jod";
        List<Permission> permissions = provider.getPermissionsForUser(username);
        assertThat(permissions.size()).isPositive();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetPermissionsForUserWhenSQLExceptionIsThrown() throws Exception {
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        when(mockdatasource.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(mockdatasource);
        provider.activate();

        String username = "jod";
        assertThrows(AuthserviceException.class, () -> {
                provider.getPermissionsForUser(username);
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testListUsersWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                provider.getUsers();
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testModifyUserWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        User dummy = new User();
        assertThrows(AuthserviceException.class, () -> {
                provider.modifyUser(dummy);
            });
    }

    @Test
    void testModifyUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> users = provider.getUsers();
        assertThat(users.size()).isPositive();
        User firstUser = users.get(0);
        User modifiedUser = new User(firstUser.getUserid(), firstUser.getUsername(), firstUser.getEmail(), "John", "Smith");
        List<User> updatedUsers = provider.modifyUser(modifiedUser);
        User updatedUser = updatedUsers.get(0);
        assertEquals(modifiedUser.getFirstname(), updatedUser.getFirstname());
        assertEquals(modifiedUser.getLastname(), updatedUser.getLastname());
    }

    @Test
    void testUpdatePassword() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        String newPassword = "zecret";
        UserAndPasswords passwords = new UserAndPasswords(user, newPassword, newPassword, false);
        List<User> updatedUsers = provider.updatePassword(passwords);
        assertEquals(users.size(), updatedUsers.size());

        // Check that the new password works
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDataSource(datasource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken(user.getUsername(), newPassword.toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);
        assertEquals(1, authenticationInfoForUser.getPrincipals().asList().size());
    }

    @Test
    void testUpdatePasswordDifferentPasswords() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, "not", "matching", false);
        assertThrows(AuthservicePasswordsNotIdenticalException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordNullPasswords() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, null, null, false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordEmptyPassword() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, "", null, false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordNullUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        UserAndPasswords passwords = new UserAndPasswords(null, "secret", "secret", false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdatePasswordWithWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        User user = new User(100, "notmatching", "nomatch@gmail.com", "Not", "Match");
        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret", false);
        assertThrows(AuthserviceException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordUserNotInDatabase() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        User user = new User(100, "notmatching", "nomatch@gmail.com", "Not", "Match");
        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret", false);
        assertThrows(AuthserviceException.class, () -> {
                provider.updatePassword(passwords);
            });
    }

    @Test
    void testAddUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<User> usersBeforeAddingOne = provider.getUsers();
        User newUser = new User(-1, "jsmith", "johnsmith31@gmail.com", "John", "Smith");
        String newUserPassword = "supersecret";
        UserAndPasswords newUserWithPasswords = new UserAndPasswords(newUser, newUserPassword, newUserPassword, false);
        List<User> users = provider.addUser(newUserWithPasswords);
        assertThat(users.size()).isGreaterThan(usersBeforeAddingOne.size());

        // Check that the password of the new user is as expected
        User user = users.stream().filter(u -> "jsmith".equals(u.getUsername())).findFirst().get();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDataSource(datasource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken(user.getUsername(), newUserPassword.toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);
        assertEquals(1, authenticationInfoForUser.getPrincipals().asList().size());
    }

    @Test
    void testAddUserWhenUsernameExists() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        User newUser = new User(-1, "admin", "admin@gmail.com", "Admin", "Istrator");
        String newUserPassword = "supersecret";
        UserAndPasswords newUserWithPasswords = new UserAndPasswords(newUser, newUserPassword, newUserPassword, false);
        assertThrows(AuthserviceException.class, () -> {
                provider.addUser(newUserWithPasswords);
            });
    }

    @Test
    void testAddUserWhenNewUserCantBeRetrievedFromTheBase() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        User newUser = new User(-1, "jod", "jod31@gmail.com", "John", "Doe");
        String newUserPassword = "supersecret";
        UserAndPasswords newUserWithPasswords = new UserAndPasswords(newUser, newUserPassword, newUserPassword, false);
        assertThrows(AuthserviceException.class, () -> {
                provider.addUser(newUserWithPasswords);
            });
    }

    @Test
    void testAddAndModifyRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<Role> originalRoles = provider.getRoles();
        assertThat(originalRoles.size()).isPositive();

        Role dummy1 = new Role(-1, "dummy", "dummy");
        List<Role> rolesAfterAdd = provider.addRole(dummy1);
        assertThat(rolesAfterAdd.size()).isGreaterThan(originalRoles.size());

        // Verify that trying to insert a new role with the same rolename fails
        Role dummy2 = new Role(-1, "dummy", "dummy");
        assertThrows(AuthserviceException.class, () -> {
                provider.addRole(dummy2);
            });

        // Modify a role
        Role roleToModify = rolesAfterAdd.stream().filter((r) -> "dummy".equals(r.getRolename())).findFirst().get();
        Role modifiedRoleToUpdate = new Role(roleToModify.getId(), "dumy", "A new description");
        List<Role> modifiedRoles = provider.modifyRole(modifiedRoleToUpdate);
        Role modifiedRole = modifiedRoles.stream().filter((r) -> roleToModify.getId() == r.getId()).findFirst().get();
        assertEquals(modifiedRoleToUpdate.getRolename(), modifiedRole.getRolename());
        assertEquals(modifiedRoleToUpdate.getDescription(), modifiedRole.getDescription());

        // Verify that changing the role name to a name already in the database fails
        Role failingToUpdate = new Role(modifiedRole.getId(), "admin", modifiedRole.getDescription());
        assertThrows(AuthserviceException.class, () -> {
                provider.modifyRole(failingToUpdate);
            });

        // Verify that trying to modify a role not present logs a warning
        // (use an id not found in the database)
        int numberOfLogMessagesBeforeUpdate = logservice.getLogmessages().size();
        Role failingToUpdate2 = new Role(1000, "dummy", modifiedRole.getDescription());
        List<Role> failedToUpdateRoles2 = provider.modifyRole(failingToUpdate2);
        assertEquals(modifiedRoles.size(), failedToUpdateRoles2.size());
        int numberOfLogMessagesAfterUpdate = logservice.getLogmessages().size();
        assertThat(numberOfLogMessagesAfterUpdate).isGreaterThan(numberOfLogMessagesBeforeUpdate);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetRolesWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                provider.getRoles();
            });
    }

    @Test
    void testAddAndModifyPermissions() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<Permission> originalPermissions = provider.getPermissions();
        assertThat(originalPermissions.size()).isPositive();

        Permission dummy1 = new Permission(-1, "dummy", "dummy");
        List<Permission> permissionsAfterAdd = provider.addPermission(dummy1);
        assertThat(permissionsAfterAdd.size()).isGreaterThan(originalPermissions.size());

        // Verify that trying to insert a new role with the same permissionname fails
        Permission dummy2 = new Permission(-1, "dummy", "dummy");
        assertThrows(AuthserviceException.class, () -> {
                provider.addPermission(dummy2);
            });

        // Modify a permission
        Permission permissionToModify = permissionsAfterAdd.stream().filter((r) -> "dummy".equals(r.getPermissionname())).findFirst().get();
        Permission modifiedPermissionToUpdate = new Permission(permissionToModify.getId(), "dumy", "A new description");
        List<Permission> modifiedPermissions = provider.modifyPermission(modifiedPermissionToUpdate);
        Permission modifiedPermission = modifiedPermissions.stream().filter((r) -> permissionToModify.getId() == r.getId()).findFirst().get();
        assertEquals(modifiedPermissionToUpdate.getPermissionname(), modifiedPermission.getPermissionname());
        assertEquals(modifiedPermissionToUpdate.getDescription(), modifiedPermission.getDescription());

        // Verify that changing the permission name to a name already in the database fails
        Permission failingToUpdate = new Permission(modifiedPermission.getId(), "user_admin_api_read", modifiedPermission.getDescription());
        assertThrows(AuthserviceException.class, () -> {
                provider.modifyPermission(failingToUpdate);
            });

        // Verify that trying to modify a permission not present logs a warning
        // (use an id not found in the database)
        int numberOfLogMessagesBeforeUpdate = logservice.getLogmessages().size();
        Permission failingToUpdate2 = new Permission(1000, "dummy", modifiedPermission.getDescription());
        List<Permission> failedToUpdatePermission2 = provider.modifyPermission(failingToUpdate2);
        assertEquals(modifiedPermissions.size(), failedToUpdatePermission2.size());
        int numberOfLogMessagesAfterUpdate = logservice.getLogmessages().size();
        assertThat(numberOfLogMessagesAfterUpdate).isGreaterThan(numberOfLogMessagesBeforeUpdate);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetPermissionsWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                provider.getPermissions();
            });
    }

    @Test
    void testAddAndModifyUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        Map<String, List<Role>> originalUserRoles = provider.getUserRoles();
        assertThat(originalUserRoles.size()).isPositive();

        // Add a new user role
        User user = provider.getUsers().get(0);
        Role newRole = provider.getRoles().stream().filter((r) -> "visitor".equals(r.getRolename())).findFirst().get();
        List<Role> originalRolesForUser = originalUserRoles.get(user.getUsername());
        UserRoles userroles = new UserRoles(user, Arrays.asList(newRole));
        Map<String, List<Role>> userRolesAfterAddingRole = provider.addUserRoles(userroles );
        assertThat(userRolesAfterAddingRole.get(user.getUsername()).size()).isGreaterThan(originalRolesForUser.size());

        // Add the same role again and verify that the role count doesn't increase
        Map<String, List<Role>> userRolesAfterAddingRole2 = provider.addUserRoles(userroles);
        assertEquals(userRolesAfterAddingRole.get(user.getUsername()).size(), userRolesAfterAddingRole2.get(user.getUsername()).size());

        // Try adding a non-existing role
        Role nonExistingRole = new Role(42, "notfound", "dummy");
        UserRoles userroles2 = new UserRoles(user, Arrays.asList(nonExistingRole));
        assertThrows(AuthserviceException.class, () -> {
                provider.addUserRoles(userroles2);
            });

        // Remove the role
        Map<String, List<Role>> userRolesAfterRemovingRole = provider.removeUserRoles(new UserRoles(user, Arrays.asList(newRole)));
        assertThat(userRolesAfterRemovingRole.get(user.getUsername()).size()).isLessThan(userRolesAfterAddingRole.get(user.getUsername()).size());

        // Try removing the role again and observe that the count is the same
        Map<String, List<Role>> userRolesAfterRemovingRole2 = provider.removeUserRoles(new UserRoles(user, Arrays.asList(newRole)));
        assertEquals(userRolesAfterRemovingRole.get(user.getUsername()).size(), userRolesAfterRemovingRole2.get(user.getUsername()).size());

        // Try removing an empty role list and observe that the count is the same
        Map<String, List<Role>> userRolesAfterRemovingRole3 = provider.removeUserRoles(new UserRoles(user, Collections.emptyList()));
        assertEquals(userRolesAfterRemovingRole.get(user.getUsername()).size(), userRolesAfterRemovingRole3.get(user.getUsername()).size());

        // Try removing a non-existing role and observe that the count is the same
        Map<String, List<Role>> userRolesAfterRemovingRole4 = provider.removeUserRoles(new UserRoles(user, Arrays.asList(nonExistingRole)));
        assertEquals(userRolesAfterRemovingRole.get(user.getUsername()).size(), userRolesAfterRemovingRole4.get(user.getUsername()).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUserRolesOperationsWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                provider.getUserRoles();
            });

        User user = new User();
        assertThrows(AuthserviceException.class, () -> {
                provider.findExistingRolesForUser(user);
            });

        UserRoles userroles = new UserRoles(new User(), Arrays.asList(new Role()));
        assertThrows(AuthserviceException.class, () -> {
                provider.removeUserRoles(userroles);
            });
    }

    @Test
    void testAddAndModifyRolesPermissions() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        Map<String, List<Permission>> originalRolesPermissions = provider.getRolesPermissions();
        assertThat(originalRolesPermissions.size()).isPositive();

        // Add a new role permission
        Role role = provider.getRoles().get(1);
        Permission newPermission = provider.getPermissions().stream().filter((r) -> "user_read".equals(r.getPermissionname())).findFirst().get();
        List<Permission> originalRolesForUser = originalRolesPermissions.get(role.getRolename());
        Map<String, List<Permission>> rolesPermissionsAfterAddingRole = provider.addRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertThat(rolesPermissionsAfterAddingRole.get(role.getRolename()).size()).isGreaterThan(originalRolesForUser.size());

        // Add the same permission again and verify that the role count doesn't increase
        Map<String, List<Permission>> rolesPermissionsAfterAddingRole2 = provider.addRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertEquals(rolesPermissionsAfterAddingRole.get(role.getRolename()).size(), rolesPermissionsAfterAddingRole2.get(role.getRolename()).size());

        // Try adding a non-existing role
        Permission nonExistingPermission = new Permission(42, "notfound", "dummy");
        RolePermissions rolepermissions = new RolePermissions(role, Arrays.asList(nonExistingPermission));
        assertThrows(AuthserviceException.class, () -> {
                provider.addRolePermissions(rolepermissions);
            });

        // Remove the role
        Map<String, List<Permission>> rolesPermissionsAfterRemovingRole = provider.removeRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertThat(rolesPermissionsAfterRemovingRole.get(role.getRolename()).size()).isLessThan(rolesPermissionsAfterAddingRole.get(role.getRolename()).size());

        // Try removing the role again and observe that the count is the same
        Map<String, List<Permission>> rolesPermissionsAfterRemovingRole2 = provider.removeRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.getRolename()).size(), rolesPermissionsAfterRemovingRole2.get(role.getRolename()).size());

        // Try removing an empty role list and observe that the count is the same
        Map<String, List<Permission>> rolesPermissionsAfterRemovingRole3 = provider.removeRolePermissions(new RolePermissions(role, Collections.emptyList()));
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.getRolename()).size(), rolesPermissionsAfterRemovingRole3.get(role.getRolename()).size());

        // Try removing a non-existing role and observe that the count is the same
        Map<String, List<Permission>> rolesPermissionsAfterRemovingRole4 = provider.removeRolePermissions(new RolePermissions(role, Arrays.asList(nonExistingPermission)));
        assertEquals(rolesPermissionsAfterRemovingRole.get(role.getRolename()).size(), rolesPermissionsAfterRemovingRole4.get(role.getRolename()).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRolesPermissionsOperationsWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        DataSource mockdatasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockdatasource.getConnection()).thenReturn(connection);
        provider.setDataSource(mockdatasource);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                provider.getRolesPermissions();
            });

        Role role = new Role();
        assertThrows(AuthserviceException.class, () -> {
                provider.findExistingPermissionsForRole(role);
            });

        RolePermissions rolepermissions = new RolePermissions(new Role(), Arrays.asList(new Permission()));
        assertThrows(AuthserviceException.class, () -> {
                provider.removeRolePermissions(rolepermissions);
            });
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}
