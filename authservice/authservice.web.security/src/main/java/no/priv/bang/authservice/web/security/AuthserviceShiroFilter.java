/*
 * Copyright 2018-2024 Steinar Bang
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

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import javax.servlet.Filter;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;

import no.priv.bang.authservice.definitions.CipherKeyService;
import no.priv.bang.authservice.web.security.shirofilter.AuthserviceShiroFilterBase;


/***
 * This class will show ups a {@link Filter} OSGi service, and will be picked
 * up by the pax web whiteboard.
 *
 * The filter maps URLs in the webapp to users and roles.
 */
@Component(service=Filter.class, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=authservice)")
@HttpWhiteboardFilterPattern("/*")
public class AuthserviceShiroFilter extends AuthserviceShiroFilterBase { // NOSONAR

    private static final Ini INI_FILE = new Ini();
    static {
        // Can't use the Ini.fromResourcePath(String) method because it can't find "shiro.ini" on the classpath in an OSGi context
        INI_FILE.load(AuthserviceShiroFilter.class.getClassLoader().getResourceAsStream("shiro.ini"));
    }

    @Reference
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Reference
    public void setSession(SessionDAO session) {
        this.session = session;
    }

    @Reference
    public void setCipherKeyService(CipherKeyService cipherKeyService) {
        this.cipherKeyService = cipherKeyService;
    }

    @Activate
    public void activate() {
        createShiroWebEnvironmentFromIniFile(getClass().getClassLoader(), INI_FILE);
    }

}
