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
package no.priv.bang.authservice.web.security.shiroconfig;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Map;

import org.apache.karaf.config.core.ConfigRepository;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.junit.jupiter.api.Test;

class ShiroConfigServiceProviderTest {

    @Test
    void testUnconfiguredComponent() {
        var configRepository = mock(ConfigRepository.class);
        var component = new ShiroConfigServiceProvider();
        component.configRepository = configRepository;
        component.activate(Collections.emptyMap());
        var timeOut = component.getGlobalSessionTimeout();
        assertThat(timeOut).isEqualTo(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
    }

    @Test
    void testConfiguredComponent() {
        var millisPerSecond = 1000L;
        var millisPerMinute = millisPerSecond * 60L;
        var millisPerHour = millisPerMinute * 60L;
        var millisPerDay = millisPerHour * 24L;
        var expectedTimeoutValue = Long.valueOf(millisPerDay * 7);
        Map<String, Object> config = Collections.singletonMap("globalSessionTimeout", expectedTimeoutValue.toString());
        var configRepository = mock(ConfigRepository.class);
        var component = new ShiroConfigServiceProvider();
        component.configRepository = configRepository;
        component.activate(config);
        var timeOut = component.getGlobalSessionTimeout();
        assertThat(timeOut).isEqualTo(expectedTimeoutValue);
    }

}
