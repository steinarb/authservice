/*
 * Copyright 2019-2021 Steinar Bang
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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.authservice.definitions.AuthservicePasswordEmptyException;
import no.priv.bang.authservice.definitions.AuthservicePasswordsNotIdenticalException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserAndPasswords;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("/password")
public class PasswordsResource extends LoggedInUserResource {

    private static final String PASSWORD_HTML = "web/password.html"; // NOSONAR No variables holding secrets here, just the name of an HTML file

    @Inject
    LogService logservice;

    @Inject
    UserManagementService useradmin;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream get() {
        return getClass().getClassLoader().getResourceAsStream(PASSWORD_HTML);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response changePasswordForCurrentUser(@FormParam("password1") String password1, @FormParam("password2") String password2) {
        try {
            Optional<User> user = findLoggedInUser(logservice, useradmin);
            if (!user.isPresent()) {
                logservice.log(LogService.LOG_ERROR, "No user in the database matching the logged in user when changing password");
                return createInternalServerErrorResponse();
            }

            UserAndPasswords passwords = UserAndPasswords.with()
                .user(user.get())
                .password1(password1)
                .password1(password2)
                .build();
            useradmin.updatePassword(passwords);

            Document html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Password successfully changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.ok().entity(html.html()).build();
        } catch (AuthservicePasswordsNotIdenticalException e) {
            Document html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Passwords not identical: password not changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.status(Status.BAD_REQUEST).entity(html.html()).build();
        } catch (AuthservicePasswordEmptyException e) {
            Document html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Passwords can't be empty: password not changed", logservice);
            fillFormValues(html, password1, password2);
            return Response.status(Status.BAD_REQUEST).entity(html.html()).build();
        } catch (AuthserviceException e) {
            return createInternalServerErrorResponse();
        }
    }


    private FormElement fillFormValues(Document html, String password1, String password2) {
        FormElement form = (FormElement) html.getElementsByTag("form").get(0);
        Elements emailInput = form.select("input[id=password1]");
        emailInput.val(password1);
        Elements firstnameInput = form.select("input[id=password2]");
        firstnameInput.val(password2);

        return form;
    }

    private Response createInternalServerErrorResponse() {
        Document html = loadHtmlFileAndSetMessage(PASSWORD_HTML, "Internal Server Error: password not changed, see karaf.log for details", logservice);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(html.html()).build();
    }


}
