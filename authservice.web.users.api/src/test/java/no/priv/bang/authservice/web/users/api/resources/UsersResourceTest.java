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
package no.priv.bang.authservice.web.users.api.resources;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;

public class UsersResourceTest {

    @Test
    void testGetUsers() {
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenReturn(createUsers());
        UsersResource resource = new UsersResource();
        resource.usermanagement = usermanagement;

        List<User> users = resource.getUsers();
        assertThat(users.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetUsersWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<User> users = resource.getUsers();
                assertEquals(0, users.size());
            });
    }

    @Test
    void testModifyUser() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenReturn(originalUsers);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<User> users = resource.modifyUser(user);
        assertEquals(originalUsers.size(), users.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testModifyUserWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<User> users = resource.modifyUser(user);
                assertEquals(originalUsers.size(), users.size());
            });
    }

    @Test
    void testUpdatePassword() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenReturn(originalUsers);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret");
        List<User> users = resource.updatePassword(passwords);
        assertEquals(originalUsers.size(), users.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdatePasswordWhenPasswordsArentIdentical() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "zecret");
        assertThrows(BadRequestException.class, () -> {
                List<User> users = resource.updatePassword(passwords);
                assertEquals(originalUsers.size(), users.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdatePasswordWhenPasswordsIsEmpty() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "", "");
        assertThrows(BadRequestException.class, () -> {
                List<User> users = resource.updatePassword(passwords);
                assertEquals(originalUsers.size(), users.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdatePasswordWhenSQLExceptionOccurs() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "", "");
        assertThrows(InternalServerErrorException.class, () -> {
                List<User> users = resource.updatePassword(passwords);
                assertEquals(originalUsers.size(), users.size());
            });
    }

    @Test
    void testAddUser() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = new User(-1, "newuser", "newuser@gmail.com", "New", "User");
        List<User> usersWithAddedUser = new ArrayList<>(originalUsers);
        usersWithAddedUser.add(user);
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenReturn(usersWithAddedUser);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret");
        List<User> users = resource.addUser(passwords);
        assertThat(users.size()).isGreaterThan(originalUsers.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddUserWhenPasswordsAreNotIdentical() {
        MockLogService logservice = new MockLogService();
        User user = new User(-1, "newuser", "newuser@gmail.com", "New", "User");
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret");
        assertThrows(BadRequestException.class, () -> {
                List<User> users = resource.addUser(passwords);
                assertEquals(0, users.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddUserWhenPasswordIsEmpty() {
        MockLogService logservice = new MockLogService();
        User user = new User(-1, "newuser", "newuser@gmail.com", "New", "User");
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordEmptyException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret");
        assertThrows(BadRequestException.class, () -> {
                List<User> users = resource.addUser(passwords);
                assertEquals(0, users.size());
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddUserWhenSQLExceptionIsThrow() {
        MockLogService logservice = new MockLogService();
        User user = new User(-1, "newuser", "newuser@gmail.com", "New", "User");
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = new UserAndPasswords(user, "secret", "secret");
        assertThrows(InternalServerErrorException.class, () -> {
                List<User> users = resource.addUser(passwords);
                assertEquals(0, users.size());
            });
    }

    @Test
    void testGetUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenReturn(createUserroles());
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<User, List<Role>> userroles = resource.getUserRoles();
        assertThat(userroles.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<User, List<Role>> userroles = resource.getUserRoles();
                assertThat(userroles.size()).isGreaterThan(0);
            });
    }

    @Test
    void testAddUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenReturn(createUserroles());
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<User, List<Role>> userroles = resource.addUserRole(new UserRoles(new User(), Arrays.asList(new Role())));
        assertThat(userroles.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<User, List<Role>> userroles = resource.addUserRole(new UserRoles(new User(), Arrays.asList(new Role())));
                assertThat(userroles.size()).isGreaterThan(0);
            });
    }

    @Test
    void testRemoveUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenReturn(createUserroles());
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<User, List<Role>> userroles = resource.removeUserRole(new UserRoles(new User(), Arrays.asList(new Role())));
        assertThat(userroles.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRemoveUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<User, List<Role>> userroles = resource.removeUserRole(new UserRoles(new User(), Arrays.asList(new Role())));
                assertThat(userroles.size()).isGreaterThan(0);
            });
    }

    public static List<User> createUsers() {
        User admin = new User(1, "admin", "admin@gmail.com", "Admin", "Istrator");
        User on = new User(2, "on", "olanordmann2345@gmail.com", "Ola", "Nordmann");
        User kn = new User(3, "kn", "karinordmann3456@gmail.com", "Kari", "Nordmann");
        User jad = new User(4, "jad", "janedoe7896@gmail.com", "Jane", "Doe");
        User jod = new User(5, "jod", "johndoe6789@gmail.com", "John", "Doe");
        return Arrays.asList(admin, on, kn, jad, jod);
    }

    public static Map<User, List<Role>> createUserroles() {
        List<User> users = createUsers();
        User admin = users.get(0);
        User on = users.get(1);
        User kn = users.get(2);
        User jad = users.get(3);
        User jod = users.get(4);
        List<Role> roles = RolesResourceTest.createRoles();
        Role adminrole = roles.get(0);
        Role caseworker = roles.get(1);
        Map<User, List<Role>> userroles = new HashMap<>();
        userroles.put(admin, Arrays.asList(adminrole, caseworker));
        userroles.put(on, Arrays.asList(adminrole, caseworker));
        userroles.put(kn, Arrays.asList(adminrole, caseworker));
        userroles.put(jad, Arrays.asList(caseworker));
        userroles.put(jod, Arrays.asList(caseworker));
        return userroles;
    }

}
