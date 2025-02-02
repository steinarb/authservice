package no.priv.bang.authservice.web.security.dbrealm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.db.liquibase.test.TestLiquibaseRunner;

/***
 * Tests for class {@link AuthserviceDbRealm}.
 */
class AuthserviceDbRealmTest {
    private static DataSource datasource;

    @BeforeAll
    static void setupForAll() throws Exception {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:authservice;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        var runner = new TestLiquibaseRunner();
        runner.activate();
        runner.prepare(datasource);
    }

    /***
     * Test a successful authentication.
     * @throws SQLException
     */
    @Test
    void testGetAuthenticationInfo() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test what happens when an SQLException is thrown
     */
    @Test
    void testGetAuthenticationInfoWhenSQLExceptionIsThrown() throws Exception {
        var realm = new AuthserviceDbRealm();
        var mockdatasource = mock(DataSource.class);
        when(mockdatasource.getConnection()).thenThrow(SQLException.class);
        realm.setDataSource(mockdatasource);
        realm.activate();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        assertThrows(AuthenticationException.class, () -> realm.getAuthenticationInfo(token));
    }

    /***
     * Test a successful authentication.
     * @throws SQLException
     */
    @Test
    void testGetAuthenticationInfoLockedUser() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("lu", "1ad".toCharArray());
        assertThrows(LockedAccountException.class, () -> realm.getAuthenticationInfo(token));
    }

    /***
     * Test authentication failing because of a wrong password.
     * @throws SQLException
     */
    @Test
    void testGetAuthenticationInfoWrongPassword() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("jad", "1add".toCharArray());

        assertThrows(IncorrectCredentialsException.class, () -> {
                realm.getAuthenticationInfo(token);
            });
    }

    /***
     * Test authentication failing because of a wrong username, i.e. user
     * not found.
     * @throws SQLException
     */
    @Test
    void testGetAuthenticationInfoWrongUsername() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("jadd", "1ad".toCharArray());

        assertThrows(UnknownAccountException.class, () -> {
                realm.getAuthenticationInfo(token);
            });
    }

    /***
     * Test authentication failing because the token is not a {@link UsernamePasswordToken}.
     */
    @Test
    void testGetAuthenticationInfoWrongTokenType() {
        var realm = new AuthserviceDbRealm();
        var token = mock(AuthenticationToken.class);
        var username = "jad";
        var password = "1ad";
        when(token.getPrincipal()).thenReturn(username);
        when(token.getCredentials()).thenReturn(password);

        assertThrows(ClassCastException.class, () -> {
                realm.getAuthenticationInfo(token);
            });
    }

    /***
     * Test that a user gets the correct roles.
     * @throws SQLException
     */
    @Test
    void testGetRolesForUsers() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var authenticationInfoForUser = realm.getAuthenticationInfo(token);

        var jadHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "caseworker");
        assertTrue(jadHasRoleUser);

        var jadHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "administrator");
        assertFalse(jadHasRoleAdministrator);
    }

    /***
     * Test that an administrator gets the correct roles.
     * @throws SQLException
     */
    @Test
    void testGetRolesForAdministrators() {
        var realm = new AuthserviceDbRealm();
        realm.setDataSource(datasource);
        realm.activate();
        var token = new UsernamePasswordToken("on", "ola12".toCharArray());
        var authenticationInfoForUser = realm.getAuthenticationInfo(token);

        var onHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "caseworker");
        assertTrue(onHasRoleUser);

        var onHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "admin");
        assertTrue(onHasRoleAdministrator);
    }

}
