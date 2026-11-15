package no.priv.bang.authservice.web.users.api.resources;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;
import static no.priv.bang.authservice.web.users.api.resources.Testdata.*;

class UsersResourceTest {

    @Test
    void testGetUsers() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenReturn(createUsers());
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var users = resource.getUsers();
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetUsersWhenExceptionIsThrown() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, resource::getUsers);
    }

    @Test
    void testModifyUser() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenReturn(originalUsers);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var users = resource.modifyUser(user);
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testModifyUserWhenExceptionIsThrown() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> resource.modifyUser(user));
    }

    @Test
    void testUnlockUser() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.unlockUser(any())).thenReturn(originalUsers);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        List<User> users = resource.unlockUser(user.username());
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testUnlockUserWithExceptionThrown() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.unlockUser(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;
        var username = user.username();

        assertThrows(InternalServerErrorException.class, () -> resource.unlockUser(username));
    }

    @Test
    void testUpdatePassword() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenReturn(originalUsers);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        var users = resource.updatePassword(passwords);
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testUpdatePasswordWhenPasswordsArentIdentical() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("zecret")
            .build();
        assertThrows(BadRequestException.class, () -> resource.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordWhenPasswordsIsEmpty() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with().user(user).password1("").password2("").build();
        assertThrows(BadRequestException.class, () -> resource.updatePassword(passwords));
    }

    @Test
    void testUpdatePasswordWhenSQLExceptionOccurs() {
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with().user(user).password1("").password2("").build();
        assertThrows(InternalServerErrorException.class, () -> resource.updatePassword(passwords));
    }

    @Test
    void testAddUser() {
        var originalUsers = createUsers();
        var user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        var usersWithAddedUser = new ArrayList<>(originalUsers);
        usersWithAddedUser.add(user);
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenReturn(usersWithAddedUser);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();
        var users = resource.addUser(passwords);
        assertThat(users).hasSizeGreaterThan(originalUsers.size());
    }

    @Test
    void testAddUserWhenPasswordsAreNotIdentical() {
        var user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();
        assertThrows(BadRequestException.class, () -> resource.addUser(passwords));
    }

    @Test
    void testAddUserWhenPasswordIsEmpty() {
        var user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthservicePasswordEmptyException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(BadRequestException.class, () -> resource.addUser(passwords));
    }

    @Test
    void testAddUserWhenSQLExceptionIsThrow() {
        var user = User.with()
            .userid(-1)
            .username("newuser")
            .email("newuser@gmail.com")
            .firstname("New")
            .lastname("User")
            .build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUser(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();
        assertThrows(InternalServerErrorException.class, () -> resource.addUser(passwords));
    }

    @Test
    void testGetUserRoles() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenReturn(Testdata.createUserroles());
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var userroles = resource.getUserRoles();
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testGetUserRolesWhenExceptionIsThrown() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, resource::getUserRoles);
    }

    @Test
    void testAddUserRoles() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenReturn(Testdata.createUserroles());
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var userroles = resource.addUserRole(UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build());
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testAddUserRolesWhenExceptionIsThrown() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var userroles = UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> resource.addUserRole(userroles));
    }

    @Test
    void testRemoveUserRoles() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenReturn(Testdata.createUserroles());
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var userroles = resource.removeUserRole(UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build());
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testRemoveUserRolesWhenExceptionIsThrown() {
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenThrow(AuthserviceException.class);
        var resource = new UsersResource();
        resource.usermanagement = usermanagement;

        var userroles = UserRoles.with().user(User.with().build()).roles(Arrays.asList(Role.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> resource.removeUserRole(userroles));
    }

}
