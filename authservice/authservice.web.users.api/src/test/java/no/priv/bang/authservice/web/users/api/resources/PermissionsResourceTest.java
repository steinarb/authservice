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
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import static no.priv.bang.authservice.web.users.api.resources.Testdata.*;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.UserManagementService;

class PermissionsResourceTest {

    @Test
    void testGetPermissions() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenReturn(createPermissions());
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var permissions = resource.getPermissions();
        assertThat(permissions).isNotEmpty();
    }

    @Test
    void testGetPermissionsWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenThrow(AuthserviceException.class);
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, resource::getPermissions);
    }

    @Test
    void testModifyPermission() {
        var logservice = new MockLogService();
        var originalPermissions = createPermissions();
        var permission = originalPermissions.stream().findFirst().get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenReturn(originalPermissions);
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var permissions = resource.modifyPermission(permission);
        assertEquals(originalPermissions.size(), permissions.size());
    }

    @Test
    void testModifyPermissionWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var originalPermissions = createPermissions();
        var permission = originalPermissions.stream().findFirst().get();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenThrow(AuthserviceException.class);
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.modifyPermission(permission);
            });
    }

    @Test
    void testAddPermission() {
        var logservice = new MockLogService();
        var originalPermissions = createPermissions();
        var permission = Permission.with().build();
        var updatedPermissions = new ArrayList<Permission>(originalPermissions);
        updatedPermissions.add(permission);
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenReturn(updatedPermissions);
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var permissions = resource.addPermission(permission);
        assertThat(permissions).hasSizeGreaterThan(originalPermissions.size());
    }

    @Test
    void testAddPermissionWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var permission = Permission.with().build();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenThrow(AuthserviceException.class);
        var resource = new PermissionsResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.addPermission(permission);
            });
    }

}
