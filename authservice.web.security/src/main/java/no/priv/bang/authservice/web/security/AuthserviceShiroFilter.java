/*
 * Copyright 2018-2020 Steinar Bang
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
package no.priv.bang.authservice.web.security;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;


/***
 * This class will show ups a {@link Filter} OSGi service, and will be picked
 * up by the pax web whiteboard.
 *
 * The filter maps URLs in the webapp to users and roles.
 */
@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN+"=/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=authservice)",
        "servletNames=authservice"},
    service=Filter.class,
    immediate=true
)
public class AuthserviceShiroFilter extends AbstractShiroFilter { // NOSONAR

    private Realm realm;
    private SessionDAO session;
    private IniWebEnvironment environment;
    private ServletContext context;
    private static final Ini INI_FILE = new Ini();
    static {
        // Can't use the Ini.fromResourcePath(String) method because it can't find "shiro.ini" on the classpath in an OSGi context
        INI_FILE.load(AuthserviceShiroFilter.class.getClassLoader().getResourceAsStream("shiro.ini"));
    }

    @Reference(target = "(osgi.web.contextname=authservice)")
    public void setServletContext(ServletContext context) {
        this.context = context;
    }

    @Reference
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Reference
    public void setSession(SessionDAO session) {
        this.session = session;
    }

    @Activate
    public void activate() {
        environment = new IniWebEnvironment();
        environment.setIni(INI_FILE);
        environment.setServletContext(context);
        environment.init();
        DefaultWebSessionManager sessionmanager = new DefaultWebSessionManager();
        sessionmanager.setSessionDAO(session);
        sessionmanager.setSessionIdUrlRewritingEnabled(false);
        DefaultWebSecurityManager securityManager = DefaultWebSecurityManager.class.cast(environment.getWebSecurityManager());
        securityManager.setSessionManager(sessionmanager);
        securityManager.setRealm(realm);
        setSecurityManager(securityManager);
        setFilterChainResolver(environment.getFilterChainResolver());
    }

}
