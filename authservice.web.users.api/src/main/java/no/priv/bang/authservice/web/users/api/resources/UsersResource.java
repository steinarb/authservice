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
package no.priv.bang.authservice.web.users.api.resources;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    @Inject
    LogService logservice;

    @Inject
    UserManagementService usermanagement;

    @GET
    @Path("/users")
    public List<User> getUsers() {
        try {
            return usermanagement.getUsers();
        } catch (AuthserviceException e) {
            logservice.log(LogService.LOG_ERROR, "User management service failed to fetch the list of all user in the database", e);
            throw new InternalServerErrorException("Failed to retrieve the list of users. See the log for details");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/modify")
    public List<User> modifyUser(User user) {
        try {
            return usermanagement.modifyUser(user);
        } catch (AuthserviceException e) {
            String message = String.format("User management service failed to modify user %s", user.getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message +". See the log for details");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/passwords/update")
    public List<User> updatePassword(UserAndPasswords passwords) {
        try {
            return usermanagement.updatePassword(passwords);
        } catch (AuthservicePasswordsNotIdenticalException e) {
            String message = String.format("Password update failure for user %s: Passwords not identical", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new BadRequestException(message);
        } catch (AuthservicePasswordEmptyException e) {
            String message = String.format("Password update failure for user %s: Passwords is empty", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new BadRequestException(message);
        } catch (AuthserviceException e) {
            String message = String.format("Password update failure for user %s: internal server error", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + ". See log file for details!");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/add")
    public List<User> addUser(UserAndPasswords passwords) {
        try {
            return usermanagement.addUser(passwords);
        } catch (AuthservicePasswordsNotIdenticalException e) {
            String message = String.format("Failed to add new user %s: Passwords not identical", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new BadRequestException(message);
        } catch (AuthservicePasswordEmptyException e) {
            String message = String.format("Failed to add new user %s: Passwords is empty", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new BadRequestException(message);
        } catch (AuthserviceException e) {
            String message = String.format("Failed to add new user %s: internal server error", passwords.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + ". See log file for details!");
        }
    }

    @GET
    @Path("/users/roles")
    public Map<User, List<Role>> getUserRoles() {
        try {
            return usermanagement.getUserRoles();
        } catch (AuthserviceException e) {
            String message = "User management service failed to retrieve the user to roles mappings";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message +". See the log for details");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/addroles")
    public Map<User, List<Role>> addUserRole(UserRoles userroles) {
        try {
            return usermanagement.addUserRoles(userroles);
        } catch (AuthserviceException e) {
            String message = String.format("User management service failed add roles to user %s", userroles.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message +". See the log for details");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/removeroles")
    public Map<User, List<Role>> removeUserRole(UserRoles userroles) {
        try {
            return usermanagement.removeUserRoles(userroles);
        } catch (AuthserviceException e) {
            String message = String.format("User management service failed remove roles to user %s", userroles.getUser().getUsername());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message +". See the log for details");
        }
    }

}
