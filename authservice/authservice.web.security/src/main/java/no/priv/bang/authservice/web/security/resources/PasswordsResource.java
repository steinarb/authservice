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

import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("/password")
public class PasswordsResource extends LoggedInUserResource {

    private static final String PASSWORD_HTML = "web/password.html"; // NOSONAR No variables holding secrets here, just the name of an HTML file

    private LogService logservice;

    Logger logger;

    @Inject
    UserManagementService useradmin;

    @Inject
    void setLogservice(LogService logservice) {
        this.logservice = logservice;
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream get() {
        return getClass().getClassLoader().getResourceAsStream(PASSWORD_HTML);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response changePasswordForCurrentUser(@FormParam("password1") String password1, @FormParam("password2") String password2) {
        try {
            var user = findLoggedInUser(logservice, useradmin);
            if (!user.isPresent()) {
                logger.error("No user in the database matching the logged in user when changing password");
                return createInternalServerErrorResponse();
            }

            var passwords = UserAndPasswords.with()
                .user(user.get())
                .password1(password1)
                .password1(password2)
                .build();
            useradmin.updatePassword(passwords);

            var html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Password successfully changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.ok().entity(html.html()).build();
        } catch (AuthservicePasswordsNotIdenticalException e) {
            var html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Passwords not identical: password not changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.status(Status.BAD_REQUEST).entity(html.html()).build();
        } catch (AuthservicePasswordEmptyException e) {
            var html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Passwords can't be empty: password not changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.status(Status.BAD_REQUEST).entity(html.html()).build();
        } catch (AuthserviceException e) {
            return createInternalServerErrorResponse();
        }
    }


    private FormElement fillFormValues(Document html, String password1, String password2) {
        var form = (FormElement) html.getElementsByTag("form").get(0);
        var emailInput = form.select("input[id=password1]");
        emailInput.val(password1);
        var firstnameInput = form.select("input[id=password2]");
        firstnameInput.val(password2);

        return form;
    }

    private Response createInternalServerErrorResponse() {
        var html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Internal Server Error: password not changed, see karaf.log for details", logservice);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(html.html()).build();
    }


}
