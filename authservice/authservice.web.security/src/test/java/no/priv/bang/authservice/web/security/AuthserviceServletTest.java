/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.authservice.web.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class AuthserviceServletTest extends ShiroTestBase {

    @Test
    void testGetRootIndexHtml() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var request = buildGetRootUrl();
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Authentication service home");
    }

    @Test
    void testGetOpenIconicCss() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var request = buildGetUrl("open-iconic/font/css/open-iconic-bootstrap.min.css");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("open-iconic");
    }

    @Test
    void testGetOpenIconicWoff() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var request = buildGetUrl("open-iconic/font/fonts/open-iconic.woff");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("wOFF");
    }

    @Test
    void testAuthenticate() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/authservice")
            .queryParam("username", "admin")
            .queryParam("password", "admin")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Login successful");
    }

    @Test
    void testAuthenticateUnknownAccount() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/authservice")
            .queryParam("username", "jjd")
            .queryParam("password", "admin")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        // Emulate DS component setup
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("unknown user");
    }

    @Test
    void testAuthenticateWrongPassword() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/authservice")
            .queryParam("username", "admin")
            .queryParam("password", "wrongpass")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Error: wrong password");
    }

    @Test
    void testGetPasswordIndexHtml() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);

        var request = buildGetPasswordUrl();
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Change password");
    }

    @Test
    void testGetUser() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));

        var request = buildGetUserUrl();
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Change information for current user");
    }

    @Test
    void testSubmitUser() throws Exception {
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var username = "jad";
        var user = User.with().userid(1).username(username).email("jane@gmail.com").firstname("Jane").lastname("Doe").build();
        when(useradmin.getUsers()).thenReturn(Arrays.asList(user));
        var updatedUser = User.with().userid(1).username(username).email("janey2017@gmail.com").firstname("Janey").lastname("Dow").build();
        when(useradmin.modifyUser(any())).thenReturn(Arrays.asList(updatedUser));

        var request = buildGetUserUrl();
        request.setMethod("POST");
        var postContentType = "application/x-www-form-urlencoded";
        request.setContentType(postContentType);
        request.setHeader("Content-Type", postContentType);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice, useradmin);

        // Ensure that the expected user is logged into shiro
        // (see the test.shiro.ini file in the test resources for available logins)
        loginUser(username, "1ad");

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Change information for current user");
    }

    private HttpServletRequest buildGetPasswordUrl() {
        var request = buildGetRootUrl();
        request.setRequestURL("http://localhost:8181/authservice/password");
        request.setRequestURI("/authservice/password/");
        return request;
    }

    private MockHttpServletRequest buildGetUserUrl() {
        var request = buildGetRootUrl();
        request.setRequestURL("http://localhost:8181/authservice/user");
        request.setRequestURI("/authservice/user/");
        return request;
    }

    private HttpServletRequest buildGetUrl(String localpath) {
        var request = buildGetRootUrl();
        request.setRequestURL("http://localhost:8181/authservice/" + localpath);
        request.setRequestURI("/authservice/" + localpath);
        return request;
    }

    private MockHttpServletRequest buildGetRootUrl() {
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setMethod("GET");
        request.setRequestURL("http://localhost:8181/authservice/");
        request.setRequestURI("/authservice/");
        request.setContextPath("/authservice");
        request.setServletPath("");
        request.setSession(session);
        return request;
    }

    private MockHttpServletRequest buildPostToLoginUrl(String originalUrl) {
        var contenttype = MediaType.APPLICATION_FORM_URLENCODED;
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setMethod("POST");
        request.setRequestURL("http://localhost:8181/authservice/login");
        request.setRequestURI("/authservice/login");
        request.setContextPath("/authservice");
        request.setServletPath("");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        request.addCookie(new Cookie("NSREDIRECT", originalUrl));
        request.addHeader("Cookie", "NSREDIRECT=" + originalUrl);
        request.setSession(session);
        return request;
    }

    private AuthserviceServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(LogService logservice, UserManagementService useradmin) throws Exception {
        var servlet = new AuthserviceServlet();
        servlet.setLogService(logservice);
        servlet.setUserManagementService(useradmin);
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.authservice.web.security.resources");
        var servletContext = mock(ServletContext.class);
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

}
