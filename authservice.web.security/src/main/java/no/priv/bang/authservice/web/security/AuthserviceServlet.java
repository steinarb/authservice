/*
 * Copyright 2018 Steinar Bang
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


import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.osgiservice.users.UserManagementService;

/***
 * This class will show ups a {@link Servlet} OSGi service, and will be picked
 * up by the pax web whiteboard.
 *
 * The servlet will respond to several URLs and will provide
 * functionality both for checking the login state, and for
 * logging in a user
 */
@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=authservice)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=authservice",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX+ServerProperties.PROVIDER_PACKAGES+"=no.priv.bang.authservice.web.security.resources"},
    service=Servlet.class,
    immediate=true
)
public class AuthserviceServlet extends ServletContainer {
    private static final long serialVersionUID = 6064420153498760622L;
    private LogService logservice;  // NOSONAR Value set by DS injection
    private UserManagementService useradmin;  // NOSONAR Value set by DS injection

    @Reference
    public void setLogservice(LogService logService) {
        this.logservice = logService;
    }

    @Reference
    public void setUserManagementService(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Activate
    public void activate() {
        // This method is called after all injections have been satisfied
    }

    @Override
    protected void init(WebConfig webConfig) throws ServletException {
        super.init(webConfig);
        ResourceConfig copyOfExistingConfig = new ResourceConfig(getConfiguration());
        copyOfExistingConfig.register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(logservice).to(LogService.class);
                    bind(useradmin).to(UserManagementService.class);
                }
            });
        reload(copyOfExistingConfig);
        Map<String, Object> configProperties = getConfiguration().getProperties();
        Set<Class<?>> classes = getConfiguration().getClasses();
        logservice.log(LogService.LOG_INFO, String.format("Authservice Jersey servlet initialized with WebConfig, with resources: %s  and config params: %s", classes.toString(), configProperties.toString()));
    }

}
