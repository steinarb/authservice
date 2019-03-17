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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.Permission;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.RolePermissions;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class RolesResource extends ResourceBase {

    @Inject
    LogService logservice;

    @Inject
    UserManagementService usermanagement;

    @GET
    @Path("/roles")
    public List<Role> getRoles() {
        try {
            return usermanagement.getRoles();
        } catch (AuthserviceException e) {
            String message = "Failed to get roles";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/role/modify")
    public List<Role> modifyRole(Role role) {
        try {
            return usermanagement.modifyRole(role);
        } catch (AuthserviceException e) {
            String message = String.format("Failed to modify role %s", role.getRolename());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/role/add")
    public List<Role> addRole(Role role) {
        try {
            return usermanagement.addRole(role);
        } catch (AuthserviceException e) {
            String message = String.format("Failed to add role %s", role.getRolename());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

    @GET
    @Path("/roles/permissions")
    public Map<String, List<Permission>> getRolesPermissions() {
        try {
            return usermanagement.getRolesPermissions();
        } catch (AuthserviceException e) {
            String message = "Failed to get all role to permission mappings";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/role/addpermissions")
    public Map<String, List<Permission>> addRolePermissions(RolePermissions rolepermissions) {
        try {
            return usermanagement.addRolePermissions(rolepermissions);
        } catch (AuthserviceException e) {
            String message = String.format("Failed to add permissions to role %s", rolepermissions.getRole().getRolename());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/role/removepermissions")
    public Map<String, List<Permission>> removeRolePermissions(RolePermissions rolepermissions) {
        try {
            return usermanagement.removeRolePermissions(rolepermissions);
        } catch (AuthserviceException e) {
            String message = String.format("Failed to remove permissions from role %s", rolepermissions.getRole().getRolename());
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

}
