package no.priv.bang.authservice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.mocks.HttpResponseForRecordingStatus;

public class LoginserviceServletProviderTest {

    abstract class MockLogService implements LogService {

        @Override
        public void log(int level, String message, Throwable exception) {
            Logger logger = LoggerFactory.getLogger(LoginserviceServletProvider.class);
            if (level == LOG_DEBUG) {
                logger.debug(message, exception);
            } else if (level == LOG_INFO) {
                logger.info(message, exception);
            } else if (level == LOG_WARNING) {
                logger.warn(message, exception);
            } else {
                logger.error(message, exception);
            }
        }

    }

    private DataSource dataSource;

    @Before
    public void setup() throws SQLException, LiquibaseException {
        DerbyDataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:ukelonn;create=true");
        dataSource = dataSourceFactory.createDataSource(properties);
        DatabaseConnection databaseConnection = new JdbcConnection(dataSource.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase("db-changelog/db-changelog.xml", classLoaderResourceAccessor, databaseConnection);
        liquibase.update("");
    }

    @Test
    public void testGet() throws ServletException, IOException {
        LogService logservice = mock(LogService.class);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);

        LoginserviceServletProvider provider = new LoginserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);
        servlet.service(request, response);

        assertThat(bodyWriter.getBuffer().toString(), containsString("name=\"username\""));
    }

    @Test
    public void testAuthenticate() throws ServletException, IOException, SQLException {
        LogService logservice = mock(MockLogService.class, Mockito.CALLS_REAL_METHODS);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);
        when(datasourceFactory.createDataSource(any(Properties.class))).thenReturn(dataSource);

        LoginserviceServletProvider provider = new LoginserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("admin");
        HttpResponseForRecordingStatus response = mock(HttpResponseForRecordingStatus.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(bodyWriter.toString(), containsString("Login successful"));
    }

    @Test
    public void testAuthenticateUnknownAccount() throws ServletException, IOException, SQLException {
        LogService logservice = mock(MockLogService.class, Mockito.CALLS_REAL_METHODS);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);
        when(datasourceFactory.createDataSource(any(Properties.class))).thenReturn(dataSource);

        LoginserviceServletProvider provider = new LoginserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("username")).thenReturn("jad");
        when(request.getParameter("password")).thenReturn("admin");
        HttpResponseForRecordingStatus response = mock(HttpResponseForRecordingStatus.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(bodyWriter.toString(), containsString("Status: Username &quot;jad&quot; not found"));
    }

    @Test
    public void testAuthenticateWrongPassword() throws ServletException, IOException, SQLException {
        LogService logservice = mock(MockLogService.class, Mockito.CALLS_REAL_METHODS);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);
        when(datasourceFactory.createDataSource(any(Properties.class))).thenReturn(dataSource);

        LoginserviceServletProvider provider = new LoginserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("wrongpass");
        HttpResponseForRecordingStatus response = mock(HttpResponseForRecordingStatus.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(bodyWriter.toString(), containsString("Status: Submitted credentials for token [org.apache.shiro.authc.UsernamePasswordToken - admin, rememberMe=true] did not match the expected credentials."));
    }

}
