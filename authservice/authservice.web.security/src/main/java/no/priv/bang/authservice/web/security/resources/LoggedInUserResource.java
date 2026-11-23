/*
 * Copyright 2019-2024 Steinar Bang
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

import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

public class LoggedInUserResource extends HtmlTemplateResource {
    private static Logger logger = LoggerFactory.getLogger(LoggedInUserResource.class);

    protected static Optional<User> findLoggedInUser(UserManagementService useradmin) {
        try {
            var subject = SecurityUtils.getSubject();
            var username = (String) subject.getPrincipal();
            var users = useradmin.getUsers();
            return users.stream().filter(u -> username.equals(u.username())).findFirst();
        } catch (Exception e) {
            var message = "Failed to find the logged in user when changing the password";
            logger.error(message, e);
            throw new AuthserviceException(message, e);
        }
    }

    protected LoggedInUserResource() {
        super();
    }

}
