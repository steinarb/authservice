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
import java.util.List;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.UserManagementService;

class PermissionsResourceTest {

    @Test
    void testGetPermissions() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenReturn(createPermissions());
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Permission> permissions = resource.getPermissions();
        assertThat(permissions.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetPermissionsWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.getPermissions()).thenThrow(AuthserviceException.class);
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Permission> permissions = resource.getPermissions();
                assertThat(permissions.size()).isGreaterThan(0);
            });
    }

    @Test
    void testModifyPermission() {
        LogService logservice = new MockLogService();
        List<Permission> originalPermissions = createPermissions();
        Permission permission = originalPermissions.stream().findFirst().get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenReturn(originalPermissions);
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Permission> permissions = resource.modifyPermission(permission);
        assertEquals(originalPermissions.size(), permissions.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testModifyPermissionWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        List<Permission> originalPermissions = createPermissions();
        Permission permission = originalPermissions.stream().findFirst().get();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyPermission(any())).thenThrow(AuthserviceException.class);
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Permission> permissions = resource.modifyPermission(permission);
                assertThat(permissions.size()).isGreaterThan(0);
            });
    }

    @Test
    void testAddPermission() {
        LogService logservice = new MockLogService();
        List<Permission> originalPermissions = createPermissions();
        Permission permission = new Permission();
        List<Permission> updatedPermissions = new ArrayList<Permission>(originalPermissions);
        updatedPermissions.add(permission);
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenReturn(updatedPermissions);
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        List<Permission> permissions = resource.addPermission(permission);
        assertThat(permissions.size()).isGreaterThan(originalPermissions.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddPermissionWhenExceptionIsThrown() {
        LogService logservice = new MockLogService();
        Permission permission = new Permission();
        UserManagementService usermanagement = mock(UserManagementService.class);
        when(usermanagement.addPermission(any())).thenThrow(AuthserviceException.class);
        PermissionsResource resource = new PermissionsResource();
        resource.logservice = logservice;
        resource.usermanagement = usermanagement;

        assertThrows(InternalServerErrorException.class, () -> {
                List<Permission> permissions = resource.addPermission(permission);
                assertThat(permissions.size()).isGreaterThan(0);
            });
    }

    public static List<Permission> createPermissions() {
        Permission user_admin_api_read = new Permission(1, "user_admin_api_read", "User admin read access");
        Permission user_admin_api_write = new Permission(2, "user_admin_api_write", "User admin write access");
        Permission caseworker_read = new Permission(3, "caseworker_read", "Caseworker read access");
        Permission caseworker_write = new Permission(4, "caseworker_write", "Caseworker write access");
        Permission user_read = new Permission(5, "user_read", "User read access");
        return Arrays.asList(user_admin_api_read, user_admin_api_write, caseworker_read, caseworker_write, user_read);
    }

}
