package no.priv.bang.authservice;

import static org.rendersnake.HtmlAttributesFactory.align;
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
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;
import org.rendersnake.ext.servlet.HtmlServletCanvas;

/***
 * This class will show ups a {@link Servlet} OSGi service, and will be picked
 * up by the pax web whiteboard.
 *
 * The servlet implements an auth service used by the nginx_auth_request module.
 * The servlet will return a 401 code if the request isn't authorized
 * and a 200 OK code if the request is authorized.
 *
 * @author Steinar Bang
 *
 */
@ServiceProperties({
    @ServiceProperty( name = ExtenderConstants.PROPERTY_URL_PATTERNS, values = {"/auth/*"}),
    @ServiceProperty( name = ExtenderConstants.PROPERTY_HTTP_CONTEXT_PATH, value = "/auth/"),
	@ServiceProperty( name = ExtenderConstants.PROPERTY_SERVLET_NAMES, value = "auth")})
public class AuthserviceServletProvider extends HttpServlet implements Provider<Servlet> {
    private static final long serialVersionUID = 6064420153498760622L;
    private LogService logService;
    private DataSourceFactory dataSourceFactory;
    UkelonnRealm realm;

    public AuthserviceServletProvider() {
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

        if (!isLoggedIn()) {
            html
                .html()
                .head().title().content("Authentication failed: need login")._head()
                .body(align("center"))
                .h1().content("Authentication failed: need login")
                ._body()
                ._html();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            html
                .html()
                .head().title().content("Successfully authenticated")._head()
                .body(align("center"))
                .h1().content("Successfully authenticated")
                ._body()
                ._html();

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private boolean isLoggedIn() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    private void logError(String message, Exception exception) {
        if (logService != null) {
            logService.log(LogService.LOG_ERROR, message, exception);
        }
    }

}
