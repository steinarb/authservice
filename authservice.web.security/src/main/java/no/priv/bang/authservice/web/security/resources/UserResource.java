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

import java.io.IOException;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

@Path("/user")
public class UserResource extends LoggedInUserResource {

    String htmlFile = "web/user.html";

    @Inject
    LogService logservice;

    @Inject
    UserManagementService useradmin;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        try {
            Optional<User> loggedInUser = findLoggedInUser(logservice, useradmin);
            if (!loggedInUser.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            Document html = loadHtmlFileAndFillForm(loggedInUser.get());
            return Response.ok().entity(html.html()).build();
        } catch (AuthserviceException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            String message = "Failed to get the user information form";
            logservice.log(LogService.LOG_ERROR, message, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public Response submit(@FormParam("email") String email, @FormParam("firstname") String firstname, @FormParam("lastname") String lastname) {
        try {
            Optional<User> loggedInUser = findLoggedInUser(logservice, useradmin);
            if (!loggedInUser.isPresent()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            int userid = loggedInUser.get().getUserid();
            String username = loggedInUser.get().getUsername();
            User userWithValuesFromForm = User.with()
                .userid(userid)
                .username(username)
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .build();
            List<User> updatedUsers = useradmin.modifyUser(userWithValuesFromForm);
            Optional<User> updatedUser = updatedUsers.stream().filter(u -> userid == u.getUserid()).findFirst();
            if (!updatedUser.isPresent()) {
                String message = String.format("Updated user not found, userid: %d  username: %s", userid, username);
                logservice.log(LogService.LOG_ERROR, message);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
            }

            Document html = loadHtmlFileAndFillForm(updatedUser.get());
            setMessage(html, "Modifications successfully saved");
            return Response.ok().entity(html.html()).build();
        } catch (AuthserviceException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            String message = "Failed to update the user data";
            logservice.log(LogService.LOG_ERROR, message, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    FormElement fillFormValues(Document html, String email, String firstname, String lastname) {
        FormElement form = (FormElement) html.getElementsByTag("form").get(0);
        Elements emailInput = form.select("input[id=email]");
        emailInput.val(email);
        Elements firstnameInput = form.select("input[id=firstname]");
        firstnameInput.val(firstname);
        Elements lastnameInput = form.select("input[id=lastname]");
        lastnameInput.val(lastname);

        return form;
    }

    private Document loadHtmlFileAndFillForm(User user) throws IOException {
        try (InputStream body = getClass().getClassLoader().getResourceAsStream(htmlFile)) {
            Document html = Jsoup.parse(body, "UTF-8", "");
            fillFormValues(html, user.getEmail(), user.getFirstname(), user.getLastname());
            return html;
        }
    }

    static void setMessage(Document html, String message) {
        Element banner = html.select("p[id=messagebanner]").get(0);
        banner.text(message);
    }

}
