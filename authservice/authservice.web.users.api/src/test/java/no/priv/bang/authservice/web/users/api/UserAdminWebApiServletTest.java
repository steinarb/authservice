/*
 * Copyright 2018-2022 Steinar Bang
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
import no.priv.bang.osgiservice.users.UserManagementService;

class UserAdminWebApiServletTest {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void testGetUsers() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenReturn(createUsers());

        HttpServletRequest request = buildGetUrl("/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetUsersWhenExceptionIsThrown() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUsers()).thenThrow(AuthserviceException.class);

        HttpServletRequest request = buildGetUrl("/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    void testModifyUser() throws Exception {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyUser(any())).thenReturn(originalUsers);

        MockHttpServletRequest request = buildPostUrl("/user/modify");
        String postBody = mapper.writeValueAsString(user);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testModifyUserWithWrongTypeInPostData() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);

        // Send a UserAndPasswords object where a User object is expected
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();

        MockHttpServletRequest request = buildPostUrl("/user/modify");
        String postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    void testUpdatePassword() throws Exception {
        MockLogService logservice = new MockLogService();
        List<User> originalUsers = createUsers();
        User user = originalUsers.stream().reduce((first, second) -> second).get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.updatePassword(any())).thenReturn(originalUsers);

        UserAndPasswords passwords = UserAndPasswords.with().user(user).password1("secret").password2("secret").build();

        MockHttpServletRequest request = buildPostUrl("/passwords/update");
        String postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertEquals(originalUsers.size(), users.size());
    }

    @Test
    void testAddUser() throws Exception {
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

        UserAndPasswords passwords = UserAndPasswords.with()
            .user(user)
            .password1("secret")
            .password2("secret")
            .build();

        MockHttpServletRequest request = buildPostUrl("/user/add");
        String postBody = mapper.writeValueAsString(passwords);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<User> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<User>>() {});
        assertThat(users).hasSizeGreaterThan(originalUsers.size());
    }

    @Test
    void testGetUserRoles() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getUserRoles()).thenReturn(createUserroles());

        HttpServletRequest request = buildGetUrl("/users/roles");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Role>> userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testAddUserRoles() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addUserRoles(any())).thenReturn(createUserroles());

        HttpServletRequest request = buildPostUrl("/user/addroles");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Role>> userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testRemoveUserRoles() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeUserRoles(any())).thenReturn(createUserroles());

        HttpServletRequest request = buildPostUrl("/user/removeroles");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Role>> userroles = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Role>>>() {});
        assertThat(userroles).isNotEmpty();
    }

    @Test
    void testGetRoles() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenReturn(createRoles());

        HttpServletRequest request = buildGetUrl("/roles");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Role> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testModifyRole() throws Exception {
        MockLogService logservice = new MockLogService();
        Role role = Role.with().id(1).rolename("somerole").description("Some role").build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenReturn(Arrays.asList(role));

        MockHttpServletRequest request = buildPostUrl("/role/modify");
        String postBody = mapper.writeValueAsString(role);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Role> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testAddRole() throws Exception {
        MockLogService logservice = new MockLogService();
        Role role = Role.with().id(1).rolename("somerole").description("Some role").build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenReturn(Arrays.asList(role));

        MockHttpServletRequest request = buildPostUrl("/role/add");
        String postBody = mapper.writeValueAsString(role);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Role> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Role>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testGetRolePermissions() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenReturn(createRolesPermissions());

        HttpServletRequest request = buildGetUrl("/roles/permissions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Permission>> rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testAddRolePermissions() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenReturn(createRolesPermissions());

        HttpServletRequest request = buildPostUrl("/role/addpermissions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Permission>> rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testRemoveRolePermissions() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenReturn(createRolesPermissions());

        HttpServletRequest request = buildPostUrl("/role/removepermissions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Map<String, List<Permission>> rolepermissions = mapper.readValue(getBinaryContent(response), new TypeReference<Map<String, List<Permission>>>() {});
        assertThat(rolepermissions).isNotEmpty();
    }

    @Test
    void testGetPermissions() throws Exception {
        MockLogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenReturn(createPermissions());

        HttpServletRequest request = buildGetUrl("/permissions");
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Permission> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testModifyPermission() throws Exception {
        MockLogService logservice = new MockLogService();
        Permission permission = Permission.with().id(1).permissionname("somepermission").description("Some permission").build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenReturn(Arrays.asList(permission));

        MockHttpServletRequest request = buildPostUrl("/permission/modify");
        String postBody = mapper.writeValueAsString(permission);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Permission> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(users).isNotEmpty();
    }

    @Test
    void testAddPermission() throws Exception {
        MockLogService logservice = new MockLogService();
        Permission permission = Permission.with().id(1).permissionname("somepermission").description("Some permission").build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenReturn(Arrays.asList(permission));

        MockHttpServletRequest request = buildPostUrl("/permission/add");
        String postBody = mapper.writeValueAsString(permission);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserAdminWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(usermanagement, logservice);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<Permission> users = mapper.readValue(getBinaryContent(response), new TypeReference<List<Permission>>() {});
        assertThat(users).isNotEmpty();
    }

    private HttpServletRequest buildGetUrl(String resource) {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildPostUrl(String resource) {
        String contenttype = MediaType.APPLICATION_JSON;
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/authservice/useradmin/api" + resource);
        request.setRequestURI("/authservice/useradmin/api" + resource);
        request.setContextPath("/authservice");
        request.setServletPath("/useradmin/api");
        request.setSession(session);
        return request;
    }

    private UserAdminWebApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(UserManagementService usermanagement, LogService logservice) throws Exception {
        UserAdminWebApiServlet servlet = new UserAdminWebApiServlet();
        servlet.setLogService(logservice);
        servlet.setUserManagementService(usermanagement);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.authservice.web.users.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/authservice");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

}
