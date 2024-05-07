/*
 * Copyright 2019-2024 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Connection.KeyVal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.FormElement;
import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.web.security.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class UserResourceTest extends ShiroTestBase {

    @Test
    void testGet() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with()
            .userid(1)
            .username(username)
            .email("jane@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.get();

        // Verify the response
        assertEquals(200, response.getStatus());
        var responsebody = (String) response.getEntity();
        var html = Jsoup.parse(responsebody);
        var form = (FormElement) html.getElementsByTag("form").get(0);
        var formdata = form.formData();
        assertThat(formdata).isNotEmpty();
        assertEquals(user.email(), findFormvalue(formdata, "email").value());
        assertEquals(user.firstname(), findFormvalue(formdata, "firstname").value());
        assertEquals(user.lastname(), findFormvalue(formdata, "lastname").value());
    }

    @Test
    void testGetNotLoggedIn() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is not logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        createNullWebSubjectAndBindItToThread();

        // Run the method under test
        var response = resource.get();

        // Verify the response
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetLoggedInUserNotFoundInAdminService() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that a user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser("jad", "1ad");

        // Run the method under test
        var response = resource.get();

        // Verify the response
        assertEquals(401, response.getStatus());
    }

    @Test
    void testGetHtmlFileNotFound() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with()
            .userid(1)
            .username(username)
            .email("jane@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.htmlFile = "web/notafile.html";
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.get();

        // Verify the response
        assertEquals(500, response.getStatus());
    }

    @Test
    void testFillFormValues() throws Exception {
        var resource = new UserResource();
        try (InputStream body = getClass().getClassLoader().getResourceAsStream("web/user.html")) {
            var html = Jsoup.parse(body, "UTF-8", "");
            var email = "jane@gmail.com";
            var firstname = "Jane";
            var lastname = "Doe";
            var form = resource.fillFormValues(html, email, firstname, lastname);
            var formdata = form.formData();
            assertEquals(email, findFormvalue(formdata, "email").value());
            assertEquals(firstname, findFormvalue(formdata, "firstname").value());
            assertEquals(lastname, findFormvalue(formdata, "lastname").value());

            // Verify that filling form with null, results in empty string form values
            resource.fillFormValues(html, null, null, null);
            formdata = form.formData();
            assertEquals("", findFormvalue(formdata, "email").value());
            assertEquals("", findFormvalue(formdata, "firstname").value());
            assertEquals("", findFormvalue(formdata, "lastname").value());
        }
    }

    @Test
    void testGetExceptionWhenFetchingUsers() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        when(useradmin.getUsers()).thenThrow(AuthserviceException.class);

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.get();

        // Verify the response
        assertEquals(401, response.getStatus()); // Should this have been a 500 rather than a 401?
    }

    @Test
    void testSubmit() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with()
            .userid(1)
            .username(username)
            .email("jane@gmail.com")
            .firstname("Jane")
            .lastname("Doe")
            .build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        var updatedUser = User.with()
            .userid(1)
            .username(username)
            .email("janey2017@gmail.com")
            .firstname("Janey")
            .lastname("Dow")
            .build();
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(updatedUser));

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(200, response.getStatus());
        var responsebody = (String) response.getEntity();
        var html = Jsoup.parse(responsebody);
        var form = (FormElement) html.getElementsByTag("form").get(0);
        var formdata = form.formData();
        assertThat(formdata).isNotEmpty();
        assertEquals(updatedUser.email(), findFormvalue(formdata, "email").value());
        assertEquals(updatedUser.firstname(), findFormvalue(formdata, "firstname").value());
        assertEquals(updatedUser.lastname(), findFormvalue(formdata, "lastname").value());
    }

    @Test
    void testSubmitLoggedInUserNotPresent() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var updatedUser = User.with()
            .userid(1)
            .username(username)
            .email("janey2017@gmail.com")
            .firstname("Janey")
            .lastname("Dow")
            .build();

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(401, response.getStatus());
    }

    @Test
    void testSubmitUserNotLoggedIn() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var updatedUser = User.with()
            .userid(1)
            .username(username)
            .email("janey2017@gmail.com")
            .firstname("Janey")
            .lastname("Dow")
            .build();

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is not logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        createNullWebSubjectAndBindItToThread();

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(401, response.getStatus());
    }

    @Test
    void testSubmitWhenUpdatedUserIsntPresent() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        var updatedUser = User.with(user).email("janey2017@gmail.com").firstname("Janey").lastname("Dow").build();
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(User.with().build()));

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(500, response.getStatus());
    }

    @Test
    void testSubmitHtmlFileNotFound() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        var updatedUser = User.with(user).email("janey2017@gmail.com").firstname("Janey").lastname("Dow").build();
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(updatedUser));

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.htmlFile = "web/notafile.html";
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(500, response.getStatus());
    }

    @Test
    void testSubmitExceptionOnUserModify() throws Exception {
        // Mock the injected OSGi services
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        var updatedUser = User.with(user).email("janey2017@gmail.com").firstname("Janey").lastname("Dow").build();
        when(useradmin.modifyUser(any())).thenThrow(AuthserviceException.class);

        // "Inject" the OSGi services into the resource
        // (done by HK2 in Jersey, google it if necessary)
        var resource = new UserResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        // Run the method under test
        var response = resource.submit(updatedUser.email(), updatedUser.firstname(), updatedUser.lastname());

        // Verify the response
        assertEquals(401, response.getStatus()); // Should this have been a 500 rather than a 401?
    }

    KeyVal findFormvalue(List<KeyVal> formdata, String fieldname) {
        return formdata.stream().filter(element -> fieldname.equals(element.key())).findFirst().get();
    }

}
