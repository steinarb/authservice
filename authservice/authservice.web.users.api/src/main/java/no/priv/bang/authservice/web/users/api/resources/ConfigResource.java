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
package no.priv.bang.authservice.web.users.api.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.UserManagementConfig;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("useradmin")
public class ConfigResource extends ResourceBase {
    private Logger logger;

    @Inject
    public UserManagementService usermanagement;

    @Inject
    public void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    @Path("/config")
    public UserManagementConfig getConfig() {
        try {
            return usermanagement.getConfig();
        } catch (AuthserviceException e) {
            var message = "User management service failed to get the list of permissions";
            logger.error(message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

    @POST
    @Path("/config")
    public UserManagementConfig modifyConfig(UserManagementConfig configToSave) {
        try {
            return usermanagement.modifyConfig(configToSave);
        } catch (AuthserviceException e) {
            var message = "User management service failed to get the list of permissions";
            logger.error(message, e);
            throw new InternalServerErrorException(message + SEE_LOG_FILE_FOR_DETAILS);
        }
    }

}
