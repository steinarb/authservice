package no.priv.bang.authservice;

import static org.rendersnake.HtmlAttributesFactory.action;
import static org.rendersnake.HtmlAttributesFactory.align;
import static org.rendersnake.HtmlAttributesFactory.dataRole;
import static org.rendersnake.HtmlAttributesFactory.for_;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import no.steria.osgi.jsr330activator.ServiceProperties;
import no.steria.osgi.jsr330activator.ServiceProperty;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;
import org.rendersnake.ext.servlet.HtmlServletCanvas;

/***
 * This class will show ups a {@link Servlet} OSGi service, and will be picked
 * up by the pax web whiteboard.
 *
 * The web page provided by the servlet service is intended to be used as a login
 * page by nginx, and will add a Shiro auth token to the cookie collection of the
 * web page.
 *
 * The web page will be accessed with the path "/login/".
 *
 * The servlet won't be started until a {@link DataSourceFactory} OSGi service
 * can be injected.
 *
 * The servlet will try a PostgreSQL JDBC connection to a database called
 * "ukelonn" on a local PostgreSQL server, and check username/password
 * against a table called "users" on that database.
 *
 * The table "users" is assumed to have the varchar columns "username", "password" and "salt", with
 * their Shiro meanings.
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({
    @ServiceProperty( name = ExtenderConstants.PROPERTY_URL_PATTERNS, values = {"/login/*"}),
    @ServiceProperty( name = ExtenderConstants.PROPERTY_HTTP_CONTEXT_PATH, value = "/login/"),
    @ServiceProperty( name = ExtenderConstants.PROPERTY_SERVLET_NAMES, value = "login")})
public class LoginserviceServletProvider extends HttpServlet implements Provider<Servlet> {
    private static final long serialVersionUID = 6064420153498760622L;
    private LogService logService;
    private DataSourceFactory dataSourceFactory;
    UkelonnRealm realm;

    public LoginserviceServletProvider() {
        realm = new UkelonnRealm();
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(1024);
        realm.setCredentialsMatcher(credentialsMatcher);
        DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Override
    public Servlet get() {
        return this;
    }

    @Inject
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Inject
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
        connectJdbcRealmToDatabase();
    }

    private void connectJdbcRealmToDatabase() {
        if (dataSourceFactory != null) {
            Properties properties = new Properties();
            properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:postgresql:///ukelonn");
            try {
                DataSource dataSource = dataSourceFactory.createDataSource(properties);
                realm.setDataSource(dataSource);
            } catch (Exception e) {
                logError("PostgreSQL database service failed to create connection to local DB server", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        HtmlServletCanvas html = new HtmlServletCanvas(request, response, response.getWriter());
        renderLoginForm(html, null, "", "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginStatusBannerText = checkLogin(username, password, response);

        response.setContentType("text/html");

        HtmlServletCanvas html = new HtmlServletCanvas(request, response, response.getWriter());
        renderLoginForm(html, loginStatusBannerText, username, password);
    }

    private String checkLogin(String username, String password, HttpServletResponse response) {
        String bannerText = "Login successful";
        response.setStatus(HttpServletResponse.SC_OK);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
        } catch(UnknownSessionException e) {
            logError("Login error: unknown session", e);
            bannerText = e.getMessage();
        } catch(UnknownAccountException e) {
            logError("Login error: unknown account", e);
            bannerText = e.getMessage();
        } catch (IncorrectCredentialsException  e) {
            logError("Login error: wrong password", e);
            bannerText = e.getMessage();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (LockedAccountException  e) {
            logError("Login error: locked account", e);
        } catch (AuthenticationException e) {
            logError("Login error: unknown error", e);
        } finally {
            token.clear();
        }

        return bannerText;
    }

    private void renderLoginForm(HtmlServletCanvas html, String loginStatusBannerText, String username, String password) throws IOException {
        String heading = loginStatusBannerText != null ? "Status: " + loginStatusBannerText : "Login";
        html
            .html()
            .head().title().content(heading)._head()
            .body(align("center"))
            .h1().content(heading)
            .form(action("/login").method("post").id("login-form"))
            .fieldset()
            .div(dataRole("fieldcontain")).label(for_("username")).content("Username")
            .input(type("text").name("username").id("username").value(username))._div()
            .div(dataRole("fieldcontain")).label(for_("password")).content("Password")
            .input(type("password").name("password").id("password").value(password))
            ._div()
            .input(type("submit").value("Login"))
            ._fieldset()
            ._form()
            ._body()
            ._html();
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
