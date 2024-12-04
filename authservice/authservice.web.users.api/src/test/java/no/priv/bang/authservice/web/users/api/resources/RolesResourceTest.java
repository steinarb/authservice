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
package no.priv.bang.authservice.web.users.api.resources;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;
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
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenReturn(createRoles());
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var roles = resource.getRoles();
        assertThat(roles).isNotEmpty();
    }

    @Test
    void testGetRolesWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRoles()).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, resource::getRoles);
    }

    @Test
    void testModifyRole() {
        var logservice = new MockLogService();
        var originalRoles = createRoles();
        var role = originalRoles.stream().findFirst().get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenReturn(originalRoles);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var roles = resource.modifyRole(role);
        assertEquals(originalRoles.size(), roles.size());
    }

    @Test
    void testModifyRoleWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var originalRoles = createRoles();
        var role = originalRoles.stream().findFirst().get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyRole(any())).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> resource.modifyRole(role));
    }

    @Test
    void testAddRole() {
        var logservice = new MockLogService();
        var originalRoles = createRoles();
        var role = Role.with().build();
        var updatedRoles = new ArrayList<Role>(originalRoles);
        updatedRoles.add(role);
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenReturn(updatedRoles);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var roles = resource.addRole(role);
        assertThat(roles).hasSizeGreaterThan(originalRoles.size());
    }

    @Test
    void testAddRoleWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var role = Role.with().build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRole(any())).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> resource.addRole(role));
    }

    @Test
    void testGetRolesPermissions() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenReturn(createRolesPermissions());
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var rolespermissions = resource.getRolesPermissions();
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testGetRolesPermissionsWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getRolesPermissions()).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, resource::getRolesPermissions);
    }

    @Test
    void testAddRolesPermissions() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenReturn(createRolesPermissions());
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var rolespermissions = resource.addRolePermissions(RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build());
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testAddRolesPermissionsWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addRolePermissions(any())).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var rolepermissions = RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> resource.addRolePermissions(rolepermissions));
    }

    @Test
    void testRemoveRolesPermissions() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenReturn(createRolesPermissions());
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var rolespermissions = resource.removeRolePermissions(RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build());
        assertThat(rolespermissions).isNotEmpty();
    }

    @Test
    void testRemoveRolesPermissionsWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.removeRolePermissions(any())).thenThrow(AuthserviceException.class);
        var resource = new RolesResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var rolepermissions = RolePermissions.with().role(Role.with().build()).permissions(Arrays.asList(Permission.with().build())).build();
        assertThrows(InternalServerErrorException.class, () -> resource.removeRolePermissions(rolepermissions));
    }

}
