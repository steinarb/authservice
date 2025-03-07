/*
 * Copyright 2018-2025 Steinar Bang
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
package no.priv.bang.authservice.web.users.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;


import no.priv.bang.authservice.definitions.AuthserviceException;
import static no.priv.bang.authservice.web.users.api.resources.Testdata.*;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementConfig;
import no.priv.bang.osgiservice.users.UserManagementService;

class UserAdminWebApiServletTest extends ShiroTestBase {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void beforeEachTest() {
        removeWebSubjectFromThread();
    }

    @Test
    void testGetUsers() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenReturn(createUsers());

        var request = buildGetUrl("/users");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetUsersWhenExceptionIsThrown() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenThrow(AuthserviceException.class);

        var request = buildGetUrl("/users");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    void testModifyUser() throws Exception {
        var logservice = new MockLogService();
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenReturn(originalUsers);

        var request = buildPostUrl("/user/modify");
        var postBody = mapper.writeValueAsString(user);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testUnlockUser() throws Exception {
        var logservice = new MockLogService();
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.unlockUser(any())).thenReturn(originalUsers);

        var request = buildGetUrl("/user/unlock/" + user.username());
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testModifyUserWithWrongTypeInPostData() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);

        // Send a UserAndPasswords object where a User object is expected
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();

        var request = buildPostUrl("/user/modify");
        var postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void testUpdatePassword() throws Exception {
        var logservice = new MockLogService();
        var originalUsers = createUsers();
        var user = originalUsers.stream().reduce((first, second) -> second).get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenReturn(originalUsers);

        var passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();

        var request = buildPostUrl("/passwords/update");
        var postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testAddUser() throws Exception {
        var logservice = new MockLogService();
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

        var passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();

        var request = buildPostUrl("/user/add");
        var postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).hasSizeGreaterThan(originalUsers.size());
    }

    @Test
    void testGetUserRoles() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenReturn(createUserroles());

        var request = buildGetUrl("/users/roles");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testAddUserRoles() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenReturn(createUserroles());

        var request = buildPostUrl("/user/addroles");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testRemoveUserRoles() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenReturn(createUserroles());

        var request = buildPostUrl("/user/removeroles");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testGetRoles() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenReturn(createRoles());

        var request = buildGetUrl("/roles");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testModifyRole() throws Exception {
        var logservice = new MockLogService();
        var role = Role.with().id(1).rolename("somerole").description("Some role").build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenReturn(Arrays.asList(role));

        var request = buildPostUrl("/role/modify");
        var postBody = mapper.writeValueAsString(role);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testAddRole() throws Exception {
        var logservice = new MockLogService();
        var role = Role.with().id(1).rolename("somerole").description("Some role").build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenReturn(Arrays.asList(role));

        var request = buildPostUrl("/role/add");
        var postBody = mapper.writeValueAsString(role);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetRolePermissions() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenReturn(createRolesPermissions());

        var request = buildGetUrl("/roles/permissions");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testAddRolePermissions() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenReturn(createRolesPermissions());

        var request = buildPostUrl("/role/addpermissions");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testRemoveRolePermissions() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenReturn(createRolesPermissions());

        var request = buildPostUrl("/role/removepermissions");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testGetPermissions() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenReturn(createPermissions());

        var request = buildGetUrl("/permissions");
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var permissions = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(permissions).isNotEmpty();
    }

    @Test
    void testModifyPermission() throws Exception {
        var logservice = new MockLogService();
        var permission = Permission.with().id(1).permissionname("somepermission").description("Some permission").build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenReturn(Arrays.asList(permission));

        var request = buildPostUrl("/permission/modify");
        var postBody = mapper.writeValueAsString(permission);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var permissions = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(permissions).isNotEmpty();
    }

    @Test
    void testAddPermission() throws Exception {
        var logservice = new MockLogService();
        var permission = Permission.with().id(1).permissionname("somepermission").description("Some permission").build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenReturn(Arrays.asList(permission));

        var request = buildPostUrl("/permission/add");
        var postBody = mapper.writeValueAsString(permission);
        request.setBodyContent(postBody);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var permissions = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(permissions).isNotEmpty();
    }

    @Test
    void testGetAndSetConfig() throws Exception {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getConfig()).thenReturn(UserManagementConfig.with().excessiveFailedLoginLimit(3).build());
        when(usermanagement.modifyConfig(any())).thenReturn(UserManagementConfig.with().excessiveFailedLoginLimit(4).build());

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");

        var request1 = buildGetUrl("/config");
        var response1 = new MockHttpServletResponse();
        servlet.service(request1, response1);
        assertEquals(HttpServletResponse.SC_OK, response1.getStatus());
        var config = mapper.readValue(getBinaryContent(response1), UserManagementConfig.class);
        assertThat(config).hasFieldOrPropertyWithValue("excessiveFailedLoginLimit", 3);

        var request2 = buildPostUrl("/config");
        var postBody = mapper.writeValueAsString(UserManagementConfig.with().excessiveFailedLoginLimit(4).build());
        request2.setBodyContent(postBody);
        var response2 = new MockHttpServletResponse();
        servlet.service(request2, response2);
        assertEquals(HttpServletResponse.SC_OK, response2.getStatus());
        var updatedConfig = mapper.readValue(getBinaryContent(response2), UserManagementConfig.class);
        assertThat(updatedConfig).hasFieldOrPropertyWithValue("excessiveFailedLoginLimit", 4);
    }

    private HttpServletRequest buildGetUrl(String resource) {
        var request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildPostUrl(String resource) {
        var contenttype = MediaType.APPLICATION_JSON;
        var request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/authservice/useradmin/api" + resource);
        request.setRequestURI("/authservice/useradmin/api" + resource);
        request.setContextPath("/authservice");
        request.setServletPath("/useradmin/api");
        request.setSession(session);
        return request;
    }

    private UserAdminWebApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(UserManagementService usermanagement, LogService logservice) throws Exception {
        var servlet = new UserAdminWebApiServlet();
        servlet.setLogService(logservice);
        servlet.setUserManagementService(usermanagement);
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.authservice.web.users.api.resources");
        var servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/authservice");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        var outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

}
