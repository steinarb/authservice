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

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("/user")
public class UserResource extends LoggedInUserResource {

    String htmlFile = "web/user.html";

    @Inject
    UserManagementService useradmin;

    private LogService logservice;

    Logger logger;

    @Inject
    void setLogservice(LogService logservice) {
        this.logservice = logservice;
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        try {
            var loggedInUser = findLoggedInUser(logservice, useradmin);
            if (!loggedInUser.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            var html = loadHtmlFileAndFillForm(loggedInUser.get());
            return Response.ok().entity(html.html()).build();
        } catch (AuthserviceException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            var message = "Failed to get the user information form";
            logger.error(message, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response submit(@FormParam("email") String email, @FormParam("firstname") String firstname, @FormParam("lastname") String lastname) {
        try {
            var loggedInUser = findLoggedInUser(logservice, useradmin);
            if (!loggedInUser.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            var userid = loggedInUser.get().userid();
            var username = loggedInUser.get().username();
            var userWithValuesFromForm = User.with()
                .userid(userid)
                .username(username)
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .build();
            var updatedUsers = useradmin.modifyUser(userWithValuesFromForm);
            var updatedUser = updatedUsers.stream().filter(u -> userid == u.userid()).findFirst();
            if (!updatedUser.isPresent()) {
                var message = String.format("Updated user not found, userid: %d  username: %s", userid, username);
                logger.error(message);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
            }

            var html = loadHtmlFileAndFillForm(updatedUser.get());
            setMessage(html, "Modifications successfully saved");
            return Response.ok().entity(html.html()).build();
        } catch (AuthserviceException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            var message = "Failed to update the user data";
            logger.error(message, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    FormElement fillFormValues(Document html, String email, String firstname, String lastname) {
        var form = (FormElement) html.getElementsByTag("form").get(0);
        var emailInput = form.select("input[id=email]");
        emailInput.val(email);
        var firstnameInput = form.select("input[id=firstname]");
        firstnameInput.val(firstname);
        var lastnameInput = form.select("input[id=lastname]");
        lastnameInput.val(lastname);

        return form;
    }

    private Document loadHtmlFileAndFillForm(User user) throws IOException {
        try (var body = getClass().getClassLoader().getResourceAsStream(htmlFile)) {
            var html = Jsoup.parse(body, "UTF-8", "");
            fillFormValues(html, user.email(), user.firstname(), user.lastname());
            return html;
        }
    }

    static void setMessage(Document html, String message) {
        var banner = html.select("p[id=messagebanner]").get(0);
        banner.text(message);
    }

}
