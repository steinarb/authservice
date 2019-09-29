/*
 * Copyright 2018 Steinar Bang
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
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.osgi.service.log.LogService;

@Path("")
public class AuthserviceResource extends HtmlTemplateResource {
    private static final String LOGIN_ERROR = "Login error: ";
    private static final String LOGIN_HTML = "web/login.html";

    @Context
    HttpHeaders httpHeaders;

    @Inject LogService logservice;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream getIndex() {
        return getClass().getClassLoader().getResourceAsStream("web/index.html");
    }

    @GET
    @Produces("text/css")
    @Path("open-iconic/font/css/open-iconic-bootstrap.min.css")
    public InputStream getOpenIconicCss() {
        return getClass().getClassLoader().getResourceAsStream("open-iconic/font/css/open-iconic-bootstrap.min.css");
    }

    @GET
    @Produces("fond/woff")
    @Path("open-iconic/font/fonts/open-iconic.woff")
    public InputStream getOpenIconicWoff() {
        return getClass().getClassLoader().getResourceAsStream("open-iconic/font/fonts/open-iconic.woff");
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public Response getLogin(@QueryParam("originalUri") String originalUri) {
        Document html = loadHtmlFile(LOGIN_HTML, logservice);
        fillFormValues(html, originalUri);

        return Response.status(Response.Status.OK).entity(html.html()).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/html")
    public Response postLogin(@FormParam("username") String username, @FormParam("password") String password, @FormParam("originalUri") String originalUri) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        try {
            subject.login(token);

            return Response.status(Response.Status.FOUND).location(URI.create(notNullUrl(originalUri))).entity("Login successful!").build();
        } catch(UnknownAccountException e) {
            String message = "unknown user";
            logservice.log(LogService.LOG_WARNING, LOGIN_ERROR + message, e);
            Document html = loadHtmlFileAndSetMessage(LOGIN_HTML, message, logservice);
            fillFormValues(html, originalUri, username, password);
            return Response.status(Response.Status.UNAUTHORIZED).entity(html.html()).build();
        } catch (IncorrectCredentialsException  e) {
            String message = "wrong password";
            logservice.log(LogService.LOG_WARNING, LOGIN_ERROR + message, e);
            Document html = loadHtmlFileAndSetMessage(LOGIN_HTML, message, logservice);
            fillFormValues(html, originalUri, username, password);
            return Response.status(Response.Status.UNAUTHORIZED).entity(html.html()).build();
        } catch (LockedAccountException  e) {
            String message = "locked account";
            logservice.log(LogService.LOG_WARNING, LOGIN_ERROR + message, e);
            Document html = loadHtmlFileAndSetMessage(LOGIN_HTML, message, logservice);
            fillFormValues(html, originalUri, username, password);
            return Response.status(Response.Status.UNAUTHORIZED).entity(html.html()).build();
        } catch (AuthenticationException e) {
            String message = "general authentication error";
            logservice.log(LogService.LOG_WARNING, LOGIN_ERROR + message, e);
            Document html = loadHtmlFileAndSetMessage(LOGIN_HTML, message, logservice);
            fillFormValues(html, originalUri, username, password);
            return Response.status(Response.Status.UNAUTHORIZED).entity(html.html()).build();
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Login error: internal server error", e);
            throw new InternalServerErrorException();
        } finally {
            token.clear();
        }
    }

    @GET
    @Path("/logout")
    @Produces("text/html")
    public Response logout() {
        Subject subject = SecurityUtils.getSubject();

        subject.logout();
        String redirectUrl = httpHeaders.getHeaderString("Referer");
        return Response.status(Response.Status.FOUND).location(URI.create(redirectUrl)).entity("Login successful!").build();
    }

    String notNullUrl(String redirectUrl) {
        if (redirectUrl == null) {
            return "";
        }

        return redirectUrl;
    }

    URI findRedirectLocation() {
        if (httpHeaders != null) {
            String originLocation = httpHeaders.getHeaderString("Origin");
            if (originLocation != null) {
                return URI.create(originLocation);
            }
        }

        return URI.create("../..");
    }

    @GET
    @Path("/check")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkLogin() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return Response.status(Response.Status.OK).entity("Successfully authenticated!\n").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("Not authenticated!\n").build();
    }

    private FormElement fillFormValues(Document html, String originalUri) {
        FormElement form = findForm(html);
        updateOriginalUri(form, originalUri);

        return form;
    }

    private FormElement fillFormValues(Document html, String originalUri, String username, String password) {
        FormElement form = findForm(html);
        updateOriginalUri(form, originalUri);
        Elements usernameInput = form.select("input[id=username]");
        usernameInput.val(username);
        Elements passwordInput = form.select("input[id=password]");
        passwordInput.val(password);

        return form;
    }

    FormElement findForm(Document html) {
        return (FormElement) html.getElementsByTag("form").get(0);
    }

    void updateOriginalUri(FormElement form, String originalUri) {
        Elements originalUriHidden = form.select("input[id=originalUri]");
        originalUriHidden.val(originalUri);
    }

}
