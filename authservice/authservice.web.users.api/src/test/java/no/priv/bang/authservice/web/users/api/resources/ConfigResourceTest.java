/*
 * Copyright 2025 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.UserManagementConfig;
import no.priv.bang.osgiservice.users.UserManagementService;

class ConfigResourceTest {

    @Test
    void testGetConfig() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getConfig()).thenReturn(UserManagementConfig.with().excessiveFailedLoginLimit(3).build());
        var resource = new ConfigResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var config = resource.getConfig();
        assertThat(config).hasFieldOrPropertyWithValue("excessiveFailedLoginLimit", 3);
    }

    @Test
    void testGetConfigWithThrownException() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.getConfig()).thenThrow(AuthserviceException.class);
        var resource = new ConfigResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        assertThatThrownBy(resource::getConfig).isInstanceOf(InternalServerErrorException.class);
    }

    @Test
    void testModifyConfig() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        var configToSave = UserManagementConfig.with().excessiveFailedLoginLimit(4).build();
        when(usermanagement.modifyConfig(any())).thenReturn(configToSave);
        var resource = new ConfigResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var config = resource.modifyConfig(configToSave);
        assertThat(config).hasFieldOrPropertyWithValue("excessiveFailedLoginLimit", 4);
    }

    @Test
    void testModifyConfigWithExceptionThrown() {
        var logservice = new MockLogService();
        var usermanagement = mock(UserManagementService.class);
        when(usermanagement.modifyConfig(any())).thenThrow(AuthserviceException.class);
        var resource = new ConfigResource();
        resource.setLogservice(logservice);
        resource.usermanagement = usermanagement;

        var configToSave = UserManagementConfig.with().excessiveFailedLoginLimit(4).build();
        assertThatThrownBy(() -> resource.modifyConfig(configToSave)).isInstanceOf(InternalServerErrorException.class);
    }

}
