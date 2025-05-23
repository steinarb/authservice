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

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.karaf.config.core.ConfigRepository;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import no.priv.bang.authservice.definitions.AuthserviceShiroConfigService;

/***
 */
@Component( immediate=true )
public class ShiroConfigServiceProvider implements AuthserviceShiroConfigService {

    static final String TIMEOUT_CONFIG_KEY = "globalSessionTimeout";

    private Long globalSessionTimeout;

    @Reference
    public ConfigRepository configRepository;

    @Activate
    public void activate(Map<String, Object> config) throws IOException {
        globalSessionTimeout = Optional.ofNullable((String)config.get(TIMEOUT_CONFIG_KEY))
            .map(c -> Long.valueOf(c))
            .orElse(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
    }

    @Override
    public long getGlobalSessionTimeout() {
        return globalSessionTimeout;
    }

}
