package no.priv.bang.authservice.web.security.dbrealm;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;

import no.priv.bang.authservice.db.derby.test.DerbyTestDatabase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

/***
 * Tests for class {@link AuthserviceDbRealm}.
 */
public class AuthserviceDbRealmTest {
    private static DerbyTestDatabase database;

    @BeforeAll
    static void setupForAll() {
        DerbyDataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        database = new DerbyTestDatabase();
        MockLogService logservice = new MockLogService();
        database.setLogservice(logservice);
        database.setDataSourceFactory(derbyDataSourceFactory);
        database.activate();
    }

    /***
     * Test a successful authentication.
     * @throws SQLException
     */
    @Test
    public void testGetAuthenticationInfo() throws SQLException {
        MockLogService logservice = new MockLogService();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDatabaseService(database);
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because of a wrong password.
     * @throws SQLException
     */
    @Test
    public void testGetAuthenticationInfoWrongPassword() throws SQLException {
        MockLogService logservice = new MockLogService();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDatabaseService(database);
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken("jad", "1add".toCharArray());

        assertThrows(IncorrectCredentialsException.class, () -> {
                AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
                assertEquals(1, authInfo.getPrincipals().asList().size());
            });
    }

    /***
     * Test authentication failing because of a wrong username, i.e. user
     * not found.
     * @throws SQLException
     */
    @Test
    public void testGetAuthenticationInfoWrongUsername() throws SQLException {
        MockLogService logservice = new MockLogService();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDatabaseService(database);
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken("jadd", "1ad".toCharArray());

        assertThrows(IncorrectCredentialsException.class, () -> {
                AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
                assertEquals(1, authInfo.getPrincipals().asList().size());
            });
        assertEquals(1, logservice.getLogmessages().size());
    }

    /***
     * Test authentication failing because the token is not a {@link UsernamePasswordToken}.
     */
    @Test
    public void testGetAuthenticationInfoWrongTokenType() {
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        AuthenticationToken token = mock(AuthenticationToken.class);
        String username = "jad";
        String password = "1ad";
        when(token.getPrincipal()).thenReturn(username);
        when(token.getCredentials()).thenReturn(password);

        assertThrows(AuthenticationException.class, () -> {
                AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
                assertEquals(1, authInfo.getPrincipals().asList().size());
            });
    }

    /***
     * Test that a user gets the correct roles.
     * @throws SQLException
     */
    @Test
    public void testGetRolesForUsers() throws SQLException {
        MockLogService logservice = new MockLogService();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDatabaseService(database);
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean jadHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "caseworker");
        assertTrue(jadHasRoleUser);

        boolean jadHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "administrator");
        assertFalse(jadHasRoleAdministrator);
    }

    /***
     * Test that an administrator gets the correct roles.
     * @throws SQLException
     */
    @Test
    public void testGetRolesForAdministrators() throws SQLException {
        MockLogService logservice = new MockLogService();
        AuthserviceDbRealm realm = new AuthserviceDbRealm();
        realm.setLogservice(logservice);
        realm.setDatabaseService(database);
        realm.activate();
        AuthenticationToken token = new UsernamePasswordToken("on", "ola12".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean onHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "caseworker");
        assertTrue(onHasRoleUser);

        boolean onHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "admin");
        assertTrue(onHasRoleAdministrator);
    }

}
