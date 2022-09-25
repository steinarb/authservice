/*
 * Copyright 2019-2022 Steinar Bang
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
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
import static no.priv.bang.authservice.web.users.api.resources.Testdata.*;

class UsersResourceTest {

    @Test
    void testGetUsers() {
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenReturn(createUsers());
        UsersResource resource = new UsersResource();
        resource.usermanagement = usermanagement;

        List<User> users = resource.getUsers();
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetUsersWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getUsers();
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
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        List<User> users = resource.modifyUser(user);
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testModifyUserWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.modifyUser(user);
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
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        List<User> users = resource.updatePassword(passwords);
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testUpdatePasswordWhenPasswordsArentIdentical() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("zecret")
            .build();
        assertThrows(BadRequestException.class, () -> {
                resource.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordWhenPasswordsIsEmpty() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("").password2("").build();
        assertThrows(BadRequestException.class, () -> {
                resource.updatePassword(passwords);
            });
    }

    @Test
    void testUpdatePasswordWhenSQLExceptionOccurs() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("").password2("").build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.updatePassword(passwords);
            });
    }

    @Test
    void testAddUser() {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        List<User> usersWithAddedUser = new ArrayList<>(originalUsers);
        usersWithAddedUser.add(user);
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenReturn(usersWithAddedUser);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();
        List<User> users = resource.addUser(passwords);
        assertThat(users).hasSizeGreaterThan(originalUsers.size());
    }

    @Test
    void testAddUserWhenPasswordsAreNotIdentical() {
        MockLogService logservice = new MockLogService();
        User user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();
        assertThrows(BadRequestException.class, () -> {
                resource.addUser(passwords);
            });
    }

    @Test
    void testAddUserWhenPasswordIsEmpty() {
        MockLogService logservice = new MockLogService();
        User user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordEmptyException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(BadRequestException.class, () -> {
                resource.addUser(passwords);
            });
    }

    @Test
    void testAddUserWhenSQLExceptionIsThrow() {
        MockLogService logservice = new MockLogService();
        User user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserAndPasswords passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.addUser(passwords);
            });
    }

    @Test
    void testGetUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenReturn(Testdata.createUserroles());
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Role>> userroles = resource.getUserRoles();
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testGetUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getUserRoles();
            });
    }

    @Test
    void testAddUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenReturn(Testdata.createUserroles());
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Role>> userroles = resource.addUserRole(UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build());
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testAddUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserRoles userroles = UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.addUserRole(userroles);
            });
    }

    @Test
    void testRemoveUserRoles() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenReturn(Testdata.createUserroles());
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Role>> userroles = resource.removeUserRole(UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build());
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testRemoveUserRolesWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenThrow(AuthserviceException.class);
        UsersResource resource = new UsersResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        UserRoles userroles = UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.removeUserRole(userroles);
            });
    }

}
