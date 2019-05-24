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
package no.priv.bang.authservice.web.security.resources;

import java.util.List;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

public class LoggedInUserResource extends HtmlTemplateResource {

    protected static Optional<User> findLoggedInUser(LogService logservice, UserManagementService useradmin) {
        try {
            Subject subject = SecurityUtils.getSubject();
            String username = (String) subject.getPrincipal();
            List<User> users = useradmin.getUsers();
            return users.stream().filter(u -> username.equals(u.getUsername())).findFirst();
        } catch (Exception e) {
            String message = "Failed to find the logged in user when changing the password";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthserviceException(message, e);
        }
    }

    protected LoggedInUserResource() {
        super();
    }

}
