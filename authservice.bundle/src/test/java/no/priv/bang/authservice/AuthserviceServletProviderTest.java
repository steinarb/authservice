package no.priv.bang.authservice;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.authservice.mocks.HttpResponseForRecordingStatus;

public class AuthserviceServletProviderTest {

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
    public void testAuthenticationFail() throws ServletException, IOException {
        LogService logservice = mock(LogService.class);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);

        AuthserviceServletProvider provider = new AuthserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        HttpResponseForRecordingStatus response = mock(HttpResponseForRecordingStatus.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);

        // Ensure that we are logged out
        SecurityUtils.getSubject().logout();
        servlet.service(request, response);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    public void testAuthenticationSucceed() throws ServletException, IOException, SQLException {
        LogService logservice = mock(LogService.class);
        DataSourceFactory datasourceFactory = mock(DataSourceFactory.class);
        when(datasourceFactory.createDataSource(any(Properties.class))).thenReturn(dataSource);

        AuthserviceServletProvider provider = new AuthserviceServletProvider();
        provider.setLogService(logservice);
        provider.setDataSourceFactory(datasourceFactory);
        Servlet servlet = provider.get();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        HttpResponseForRecordingStatus response = mock(HttpResponseForRecordingStatus.class, Mockito.CALLS_REAL_METHODS);
        StringWriter bodyWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);

        // Ensure that we are logged in
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "admin".toCharArray(), true);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        servlet.service(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

}
