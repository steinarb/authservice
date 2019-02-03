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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import static no.priv.bang.authservice.web.users.api.resources.PermissionsResourceTest.*;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.UserManagementService;

public class RolesResourceTest {

    @Test
    void testGetRoles() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenReturn(createRoles());
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.getRoles();
        assertThat(roles.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetRolesWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Role> roles = resource.getRoles();
                assertThat(roles.size()).isGreaterThan(0);
            });
    }

    @Test
    void testModifyRole() {
        LogService logservice = new MockLogService();
        List<Role> originalRoles = createRoles();
        Role role = originalRoles.stream().findFirst().get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenReturn(originalRoles);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.modifyRole(role);
        assertEquals(originalRoles.size(), roles.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testModifyRoleWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        List<Role> originalRoles = createRoles();
        Role role = originalRoles.stream().findFirst().get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Role> roles = resource.modifyRole(role);
                assertThat(roles.size()).isGreaterThan(0);
            });
    }

    @Test
    void testAddRole() {
        LogService logservice = new MockLogService();
        List<Role> originalRoles = createRoles();
        Role role = new Role();
        List<Role> updatedRoles = new ArrayList<Role>(originalRoles);
        updatedRoles.add(role);
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenReturn(updatedRoles);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.addRole(role);
        assertThat(roles.size()).isGreaterThan(originalRoles.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddRoleWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        Role role = new Role();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Role> roles = resource.addRole(role);
                assertThat(roles.size()).isGreaterThan(0);
            });
    }

    @Test
    void testGetRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.getRolesPermissions();
        assertThat(rolespermissions.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<String, List<Permission>> rolespermissions = resource.getRolesPermissions();
                assertThat(rolespermissions.size()).isGreaterThan(0);
            });
    }

    @Test
    void testAddRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.addRolePermissions(new RolePermissions(new Role(), Arrays.asList(new Permission())));
        assertThat(rolespermissions.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<String, List<Permission>> rolespermissions = resource.addRolePermissions(new RolePermissions(new Role(), Arrays.asList(new Permission())));
                assertThat(rolespermissions.size()).isGreaterThan(0);
            });
    }

    @Test
    void testRemoveRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.removeRolePermissions(new RolePermissions(new Role(), Arrays.asList(new Permission())));
        assertThat(rolespermissions.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testRemoveRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                Map<String, List<Permission>> rolespermissions = resource.removeRolePermissions(new RolePermissions(new Role(), Arrays.asList(new Permission())));
                assertThat(rolespermissions.size()).isGreaterThan(0);
            });
    }

    public static List<Role> createRoles() {
        Role admin = new Role(1, "admin", "Administrate stuff");
        Role caseworker = new Role(2, "caseworker", "Respond to cases");
        Role visitor = new Role(3, "visitor", "Just browsing");
        return Arrays.asList(admin, caseworker, visitor);
    }

    public static Map<String, List<Permission>> createRolesPermissions() {
        List<Role> roles = createRoles();
        Role admin = roles.get(0);
        Role caseworker = roles.get(1);
        List<Permission> permissions = createPermissions();
        Permission user_admin_api_read = permissions.get(0);
        Permission user_admin_api_write = permissions.get(1);
        Permission caseworker_read = permissions.get(2);
        Permission caseworker_write = permissions.get(3);
        Permission user_read = permissions.get(4);
        Map<String, List<Permission>> rolespermissions = new HashMap<>();
        rolespermissions.put(admin.getRolename(), Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read));
        rolespermissions.put(caseworker.getRolename(), Arrays.asList(caseworker_read, caseworker_write, user_read));
        return rolespermissions;
    }

}
