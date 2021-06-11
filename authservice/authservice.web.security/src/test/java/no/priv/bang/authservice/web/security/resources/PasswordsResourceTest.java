/*
 * Copyright 2019-2021 Steinar Bang
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

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
    void testGet() {
        PasswordsResource resource = new PasswordsResource();

        InputStream htmlfile = resource.get();
        String html = new BufferedReader(new InputStreamReader(htmlfile)).lines().collect(Collectors.joining("+n"));
        assertThat(html).startsWith("<html");
    }

    @Test
    void testChangePasswordForCurrentUser() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "secret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserPasswordsDontMatch() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordsNotIdenticalException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "zecret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserPasswordsIsEmpty() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        loginUser(username, "1ad");
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "";
        String password2 = "";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithNoSecurityContext() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        ThreadContext.remove();
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "secret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithNullPrincipal() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        createSubjectWithNullPrincipalAndBindItToThread();
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "secret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentWithUserNotLoggedIn() {
        int userid = 2;
        String username = "jad";
        String email = "jad@gmail.com";
        String firstname = "Jane";
        String lastname = "Doe";
        User user = User.with().userid(userid).username(username).email(email).firstname(firstname).lastname(lastname).build();
        createSubjectAndBindItToThread();
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        when(useradmin.updatePassword(any())).thenThrow(AuthservicePasswordEmptyException.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "secret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

    @Test
    void testChangePasswordForCurrentUserWhenUserFromPrincipalCantBeFoundInUsersList() {
        String username = "jad";
        loginUser(username, "1ad");
        PasswordsResource resource = new PasswordsResource();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        String password1 = "secret";
        String password2 = "secret";

        Response response = resource.changePasswordForCurrentUser(password1, password2);
        assertEquals(500, response.getStatus());
    }

}
