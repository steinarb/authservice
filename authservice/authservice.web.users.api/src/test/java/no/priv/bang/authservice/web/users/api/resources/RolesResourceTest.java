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

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import static no.priv.bang.authservice.web.users.api.resources.Testdata.*;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.UserManagementService;

class RolesResourceTest {

    @Test
    void testGetRoles() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenReturn(createRoles());
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.getRoles();
        assertThat(roles).isNotEmpty();
    }

    @Test
    void testGetRolesWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getRoles();
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
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.modifyRole(role);
        assertEquals(originalRoles.size(), roles.size());
    }

    @Test
    void testModifyRoleWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        List<Role> originalRoles = createRoles();
        Role role = originalRoles.stream().findFirst().get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.modifyRole(role);
            });
    }

    @Test
    void testAddRole() {
        LogService logservice = new MockLogService();
        List<Role> originalRoles = createRoles();
        Role role = Role.with().build();
        List<Role> updatedRoles = new ArrayList<Role>(originalRoles);
        updatedRoles.add(role);
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenReturn(updatedRoles);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        List<Role> roles = resource.addRole(role);
        assertThat(roles).hasSizeGreaterThan(originalRoles.size());
    }

    @Test
    void testAddRoleWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        Role role = Role.with().build();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.addRole(role);
            });
    }

    @Test
    void testGetRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.getRolesPermissions();
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testGetRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getRolesPermissions();
            });
    }

    @Test
    void testAddRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.addRolePermissions(RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build());
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testAddRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        RolePermissions rolepermissions = RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.addRolePermissions(rolepermissions);
            });
    }

    @Test
    void testRemoveRolesPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenReturn(createRolesPermissions());
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        Map<String, List<Permission>> rolespermissions = resource.removeRolePermissions(RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build());
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testRemoveRolesPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenThrow(AuthserviceException.class);
        RolesResource resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        RolePermissions rolepermissions = RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.removeRolePermissions(rolepermissions);
            });
    }

}
