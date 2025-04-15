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
package no.priv.bang.authservice.web.security.shirofilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.db.liquibase.test.TestLiquibaseRunner;
import no.priv.bang.authservice.definitions.CipherKeyService;
import no.priv.bang.authservice.web.security.dbrealm.AuthserviceDbRealm;

public class AuthserviceShiroFilterBaseTest {

    private static MemorySessionDAO session = new MemorySessionDAO();
    private static AuthserviceDbRealm realm;
    private static ServletContext context;

    @BeforeAll
    static void setup() throws SQLException {
        var dataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:authservice;create=true");
        var datasource = dataSourceFactory.createDataSource(properties);
        var runner = new TestLiquibaseRunner();
        runner.activate();
        runner.prepare(datasource);
        realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        context = mock(ServletContext.class);
        when(context.getContextPath()).thenReturn("/authservice");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCreateShiroFilter() throws Exception {
        var cipherKeyService = mock(CipherKeyService.class);
        var filter = new AuthserviceShiroFilterBase();
        filter.realm = realm;
        filter.session = session;
        filter.cipherKeyService = cipherKeyService;
        var classLoader = getClass().getClassLoader();
        var ini = new Ini();
        ini.load(classLoader.getResourceAsStream("test.shiro.ini"));
        filter.createShiroWebEnvironmentFromIniFile(classLoader, ini);

        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        var response = mock(HttpServletResponse.class, Mockito.CALLS_REAL_METHODS);
        var bodyWriter = new StringWriter();
        var responseWriter = new PrintWriter(bodyWriter);
        when(response.getWriter()).thenReturn(responseWriter);

        // Get the security manager from the filter and log in
        // to verify that the filter setup is working
        var securitymanager = filter.getSecurityManager();
        var token = new UsernamePasswordToken("admin", "admin".toCharArray(), true);
        var info = securitymanager.authenticate(token);
        assertThat(info.getPrincipals().asList()).hasSize(1);
    }

}
