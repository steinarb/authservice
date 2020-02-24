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
package no.priv.bang.authservice.web.users.frontend;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

import no.priv.bang.servlet.frontend.FrontendServlet;

@Component(
    property= {
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/useradmin/*",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT + "=(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME +"=authservice)",
        HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME+"=useradmin"},
    service=Servlet.class,
    immediate=true
)
public class UserAdminServlet extends FrontendServlet {
    private static final long serialVersionUID = -3496606785818930881L;

    public UserAdminServlet() {
        super();
        setRoutes(
            "/",
            "/users",
            "/users/modify",
            "/users/roles",
            "/users/passwords",
            "/users/add",
            "/roles",
            "/roles/modify",
            "/roles/permissions",
            "/roles/add",
            "/permissions",
            "/permissions/modify",
            "/permissions/add");
    }

    @Override
    @Reference
    public void setLogService(LogService logservice) {
        super.setLogService(logservice);
    }

}
