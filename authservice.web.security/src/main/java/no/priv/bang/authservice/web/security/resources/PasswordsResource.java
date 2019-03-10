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

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("/password")
public class PasswordsResource {

    @Inject
    LogService logservice;

    @Inject
    UserManagementService useradmin;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream get() {
        return getClass().getClassLoader().getResourceAsStream("web/password.html");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response changePasswordForCurrentUser(@FormParam("password1") String password1, @FormParam("password2") String password2) {
        try {
            Optional<User> user = findLoggedInUser();
            if (!user.isPresent()) {
                logservice.log(LogService.LOG_ERROR, "No user in the database matching the logged in user when changing password");
                return createInternalServerErrorResponse();
            }

            UserAndPasswords passwords = new UserAndPasswords(user.get(), password1, password2, false);
            useradmin.updatePassword(passwords);

            return Response.ok().entity(getClass().getClassLoader().getResourceAsStream("web/password_successful_change.html")).build();
        } catch (AuthservicePasswordsNotIdenticalException e) {
            return Response.status(Status.BAD_REQUEST).entity(getClass().getClassLoader().getResourceAsStream("web/password_passwords_not_identical.html")).build();
        } catch (AuthservicePasswordEmptyException e) {
            return Response.status(Status.BAD_REQUEST).entity(getClass().getClassLoader().getResourceAsStream("web/password_passwords_cant_be_empty.html")).build();
        } catch (AuthserviceException e) {
            return createInternalServerErrorResponse();
        }
    }


    private Response createInternalServerErrorResponse() {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(getClass().getClassLoader().getResourceAsStream("web/password_internal_server_error.html")).build();
    }


    Optional<User> findLoggedInUser() {
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


}
