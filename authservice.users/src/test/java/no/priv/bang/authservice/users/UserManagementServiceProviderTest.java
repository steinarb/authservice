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
package no.priv.bang.authservice.users;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import static org.assertj.core.api.Assertions.*;

import no.priv.bang.authservice.db.derby.test.DerbyTestDatabase;
import no.priv.bang.authservice.definitions.AuthserviceDatabaseService;
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

public class UserManagementServiceProviderTest {
    private static DerbyTestDatabase database;

    @BeforeAll
    static void setupForAll() {
        DerbyDataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        database = new DerbyTestDatabase();
        MockLogService logservice = new MockLogService();
        database.setLogservice(logservice);
        database.setDataSourceFactory(derbyDataSourceFactory);
        database.activate();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testListUsersWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                List<User> users = provider.getUsers();
                assertEquals(0, users.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testModifyUserWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        User dummy = new User();
        assertThrows(AuthserviceException.class, () -> {
                List<User> users = provider.modifyUser(dummy);
                assertEquals(0, users.size());
            });
    }

    @Test
    void testModifyUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        assertThat(users.size()).isGreaterThan(0);
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
        provider.setDatabase(database);
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
        realm.setDatabaseService(database);
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
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, "not", "matching", false);
        assertThrows(AuthservicePasswordsNotIdenticalException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @Test
    void testUpdatePasswordNullPasswords() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, null, null, false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @Test
    void testUpdatePasswordEmptyPassword() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = users.get(0);
        UserAndPasswords passwords = new UserAndPasswords(user, "", null, false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @Test
    void testUpdatePasswordNullUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        UserAndPasswords passwords = new UserAndPasswords(null, "secret", "secret", false);
        assertThrows(AuthservicePasswordEmptyException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdatePasswordWithWhenSQLExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        List<User> users = Collections.emptyList();
        User user = new User(100, "notmatching", "nomatch@gmail.com", "Not", "Match");
        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret", false);
        assertThrows(AuthserviceException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @Test
    void testUpdatePasswordUserNotInDatabase() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<User> users = provider.getUsers();
        User user = new User(100, "notmatching", "nomatch@gmail.com", "Not", "Match");
        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret", false);
        assertThrows(AuthserviceException.class, () -> {
                List<User> updatedUsers = provider.updatePassword(passwords);
                assertEquals(users.size(), updatedUsers.size());
            });
    }

    @Test
    public void testAddUser() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
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
        realm.setDatabaseService(database);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken(user.getUsername(), newUserPassword.toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);
        assertEquals(1, authenticationInfoForUser.getPrincipals().asList().size());
    }

    @Test
    public void testAddUserWhenUsernameExists() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        User newUser = new User(-1, "admin", "admin@gmail.com", "Admin", "Istrator");
        String newUserPassword = "supersecret";
        UserAndPasswords newUserWithPasswords = new UserAndPasswords(newUser, newUserPassword, newUserPassword, false);
        assertThrows(AuthserviceException.class, () -> {
                List<User> users = provider.addUser(newUserWithPasswords);
                assertEquals(0, users.size());
            });
    }

    @Test
    void testAddUserWhenNewUserCantBeRetrievedFromTheBase() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        User newUser = new User(-1, "jod", "jod31@gmail.com", "John", "Doe");
        String newUserPassword = "supersecret";
        UserAndPasswords newUserWithPasswords = new UserAndPasswords(newUser, newUserPassword, newUserPassword, false);
        assertThrows(AuthserviceException.class, () -> {
                List<User> users = provider.addUser(newUserWithPasswords);
                assertEquals(0, users.size());
            });
    }

    @Test
    void testAddAndModifyRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<Role> originalRoles = provider.getRoles();
        assertThat(originalRoles.size()).isGreaterThan(0);

        Role dummy1 = new Role(-1, "dummy", "dummy");
        List<Role> rolesAfterAdd = provider.addRole(dummy1);
        assertThat(rolesAfterAdd.size()).isGreaterThan(originalRoles.size());

        // Verify that trying to insert a new role with the same rolename fails
        Role dummy2 = new Role(-1, "dummy", "dummy");
        assertThrows(AuthserviceException.class, () -> {
                List<Role> rolesAfterAdd2 = provider.addRole(dummy2);
                assertEquals(rolesAfterAdd.size(), rolesAfterAdd2.size());
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
                List<Role> failedToUpdateRoles = provider.modifyRole(failingToUpdate);
                assertEquals(modifiedRoles.size(), failedToUpdateRoles.size());
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
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                List<Role> roles = provider.getRoles();
                assertEquals(0, roles.size());
            });
    }

    @Test
    void testAddAndModifyPermissions() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        List<Permission> originalPermissions = provider.getPermissions();
        assertThat(originalPermissions.size()).isGreaterThan(0);

        Permission dummy1 = new Permission(-1, "dummy", "dummy");
        List<Permission> permissionsAfterAdd = provider.addPermission(dummy1);
        assertThat(permissionsAfterAdd.size()).isGreaterThan(originalPermissions.size());

        // Verify that trying to insert a new role with the same permissionname fails
        Permission dummy2 = new Permission(-1, "dummy", "dummy");
        assertThrows(AuthserviceException.class, () -> {
                List<Permission> permissionsAfterAdd2 = provider.addPermission(dummy2);
                assertEquals(permissionsAfterAdd.size(), permissionsAfterAdd2.size());
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
                List<Permission> failedToUpdatePermissions = provider.modifyPermission(failingToUpdate);
                assertEquals(modifiedPermissions.size(), failedToUpdatePermissions.size());
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
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                List<Permission> permissions = provider.getPermissions();
                assertEquals(0, permissions.size());
            });
    }

    @Test
    void testAddAndModifyUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        Map<String, List<Role>> originalUserRoles = provider.getUserRoles();
        assertThat(originalUserRoles.size()).isGreaterThan(0);

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
        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Role>> userRolesAfterAddingRole3 = provider.addUserRoles(new UserRoles(user, Arrays.asList(nonExistingRole)));
                assertEquals(0, userRolesAfterAddingRole3.size());
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
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Role>> userroles = provider.getUserRoles();
                assertEquals(0, userroles.size());
            });

        assertThrows(AuthserviceException.class, () -> {
                Set<String> role_names = provider.findExistingRolesForUser(new User());
                assertEquals(0, role_names.size());
            });

        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Role>> userroles = provider.removeUserRoles(new UserRoles(new User(), Arrays.asList(new Role())));
                assertEquals(0, userroles.size());
            });
    }

    @Test
    void testAddAndModifyRolesPermissions() {
        MockLogService logservice = new MockLogService();
        UserManagementServiceProvider provider = new UserManagementServiceProvider();
        provider.setLogservice(logservice);
        provider.setDatabase(database);
        provider.activate();

        Map<String, List<Permission>> originalRolesPermissions = provider.getRolesPermissions();
        assertThat(originalRolesPermissions.size()).isGreaterThan(0);

        // Add a new role permission
        Role role = provider.getRoles().get(0);
        Permission newPermission = provider.getPermissions().stream().filter((r) -> "user_read".equals(r.getPermissionname())).findFirst().get();
        List<Permission> originalRolesForUser = originalRolesPermissions.get(role.getRolename());
        Map<String, List<Permission>> rolesPermissionsAfterAddingRole = provider.addRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertThat(rolesPermissionsAfterAddingRole.get(role.getRolename()).size()).isGreaterThan(originalRolesForUser.size());

        // Add the same permission again and verify that the role count doesn't increase
        Map<String, List<Permission>> rolesPermissionsAfterAddingRole2 = provider.addRolePermissions(new RolePermissions(role, Arrays.asList(newPermission)));
        assertEquals(rolesPermissionsAfterAddingRole.get(role.getRolename()).size(), rolesPermissionsAfterAddingRole2.get(role.getRolename()).size());

        // Try adding a non-existing role
        Permission nonExistingPermission = new Permission(42, "notfound", "dummy");
        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Permission>> rolesPermissionsAfterAddingRole3 = provider.addRolePermissions(new RolePermissions(role, Arrays.asList(nonExistingPermission)));
                assertEquals(0, rolesPermissionsAfterAddingRole3.size());
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
        AuthserviceDatabaseService mockbase = mock(AuthserviceDatabaseService.class);
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);
        when(mockbase.getConnection()).thenReturn(connection);
        provider.setDatabase(mockbase);
        provider.activate();

        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Permission>> rolesPermissions = provider.getRolesPermissions();
                assertEquals(0, rolesPermissions.size());
            });

        assertThrows(AuthserviceException.class, () -> {
                Set<String> existingPermissions = provider.findExistingPermissionsForRole(new Role());
                assertEquals(0, existingPermissions.size());
            });

        assertThrows(AuthserviceException.class, () -> {
                Map<String, List<Permission>> rolespermissions = provider.removeRolePermissions(new RolePermissions(new Role(), Arrays.asList(new Permission())));
                assertEquals(0, rolespermissions.size());
            });
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}
