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
package no.priv.bang.authservice.web.security.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.authservice.web.security.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class PasswordsResourceTest extends ShiroTestBase {

    @Test
    void testGet() throws Exception {
        var resource = new PasswordsResource();

        var htmlfile = resource.get();
        try(var reader = new BufferedReader(new InputStreamReader(htmlfile))) {
            var html = reader.lines().collect(Collectors.joining("+n"));
            assertThat(html).startsWith("<html");
        }
    }

    @Test
    void testChangePasswordForCurrentUser() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "secret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserPasswordsDontMatch() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "zecret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserPasswordsIsEmpty() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "";
        var password2 = "";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithNoSecurityContext() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        ThreadContext.remove();
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "secret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithNullPrincipal() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        createSubjectWithNullPrincipalAndBindItToThread();
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "secret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithUserNotLoggedIn() {
        var userid = 2;
        var username = "jad";
        var email = "jad@gmail.com";
        var firstname = "Jane";
        var lastname = "Doe";
        var user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        createSubjectAndBindItToThread();
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "secret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserWhenUserFromPrincipalCantBeFoundInUsersList() {
        var username = "jad";
        loginUser(username, "1ad");
        var resource = new PasswordsResource();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        var password1 = "secret";
        var password2 = "secret";

        var response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

}
