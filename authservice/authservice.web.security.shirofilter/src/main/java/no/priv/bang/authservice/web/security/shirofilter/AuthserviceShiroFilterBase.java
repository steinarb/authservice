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
package no.priv.bang.authservice.web.security.shirofilter;

import org.apache.shiro.config.Ini;
import org.apache.shiro.lang.util.ClassUtils;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import no.priv.bang.authservice.definitions.AuthserviceShiroConfigService;
import no.priv.bang.authservice.definitions.CipherKeyService;

/***
 * A base class for shiro filters for a web context created from INI files
 *
 * The {@link #createShiroWebEnvironmentFromIniFile} method must be called by the subclass
 * after {@link #realm}, {@link #session} and {@link #cipherKeyService} have been set
 */
public class AuthserviceShiroFilterBase extends AbstractShiroFilter {

    // Dependency injected shiro services
    protected Realm realm;
    protected SessionDAO session;
    protected CipherKeyService cipherKeyService;
    protected AuthserviceShiroConfigService shiroConfigService;

    /***
     * Create a {@link IniWebEnvironment} from an INI file and connect it to the current web context.
     *
     * @param classLoader a {@link ClassLoader} that can find all classes the INI file wants to instantiate
     * @param iniFile an {@link Ini} instance holding a parsed shiro ini file
     */
    protected void createShiroWebEnvironmentFromIniFile(ClassLoader classLoader, Ini iniFile) {
        try {
            ClassUtils.setAdditionalClassLoader(classLoader); // Set class loader that can find PassThruAuthenticationFilter for the Shiro INI parser
            var environment = createShiroIniWebEnvironment();
            environment.setIni(iniFile);
            environment.setServletContext(getServletContext());
            environment.init();
            var sessionmanager = new DefaultWebSessionManager();
            sessionmanager.setSessionDAO(session);
            sessionmanager.setSessionIdUrlRewritingEnabled(false);
            sessionmanager.setGlobalSessionTimeout(shiroConfigService.getGlobalSessionTimeout());
            var securityManager = DefaultWebSecurityManager.class.cast(environment.getWebSecurityManager());
            securityManager.setSessionManager(sessionmanager);
            securityManager.setRealm(realm);
            var remembermeManager = (AbstractRememberMeManager) securityManager.getRememberMeManager();
            remembermeManager.setCipherKey(cipherKeyService.getCipherKey());
            setSecurityManager(securityManager);
            setFilterChainResolver(environment.getFilterChainResolver());
        } finally {
            ClassUtils.removeAdditionalClassLoader();
        }
    }

    /**
     * Override this method to use a different web environment class
     * @return an instance of {@link IniWebEnvironment} or a subclass of {@link IniWebEnvironment}
     */
    protected IniWebEnvironment createShiroIniWebEnvironment() {
        return new IniWebEnvironment();
    }

}
