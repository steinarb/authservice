package no.priv.bang.authservice;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/***
 * Tests for class {@link UkelonnRealm}.
 *
 * @author Steinar Bang
 *
 */
public class UkelonnRealmTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /***
     * Test a successful authentication.
     * @throws SQLException
     */
    @Test
    public void testGetAuthenticationInfo() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultset = mock(ResultSet.class);
        when(resultset.next()).thenReturn(true).thenReturn(false);
        when(resultset.getString(eq("password"))).thenReturn("adEp0s7cQHrJcIsDTDoFQK8eUAIFGh23wew7Klis1sk=");
        when(resultset.getString(eq("salt"))).thenReturn("78dCvMDECA47YMdtCkgkwQ==");
        when(statement.executeQuery()).thenReturn(resultset);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(dataSource.getConnection()).thenReturn(connection);

        UkelonnRealm realm = new UkelonnRealm();
        realm.setDataSource(dataSource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
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
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultset = mock(ResultSet.class);
        when(resultset.next()).thenReturn(true).thenReturn(false);
        when(resultset.getString(eq("password"))).thenReturn("adEp0s7cQHrJcIsDTDoFQK8eUAIFGh23wew7Klis1sk=");
        when(resultset.getString(eq("salt"))).thenReturn("78dCvMDECA47YMdtCkgkwQ==");
        when(statement.executeQuery()).thenReturn(resultset);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(dataSource.getConnection()).thenReturn(connection);

        UkelonnRealm realm = new UkelonnRealm();
        realm.setDataSource(dataSource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jad", "1add".toCharArray());

        exception.expect(IncorrectCredentialsException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because of a wrong username, i.e. user
     * not found.
     * @throws SQLException
     */
    @Test
    public void testGetAuthenticationInfoWrongUsername() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultset = mock(ResultSet.class);
        when(resultset.next()).thenReturn(false);
        when(statement.executeQuery()).thenReturn(resultset);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(dataSource.getConnection()).thenReturn(connection);

        UkelonnRealm realm = new UkelonnRealm();
        realm.setDataSource(dataSource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jadd", "1ad".toCharArray());

        exception.expect(IncorrectCredentialsException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test authentication failing because the token is not a {@link UsernamePasswordToken}.
     */
    @Test
    public void testGetAuthenticationInfoWrongTokenType() {
        UkelonnRealm realm = new UkelonnRealm();
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = mock(AuthenticationToken.class);
        String username = "jad";
        String password = "1ad";
        when(token.getPrincipal()).thenReturn(username);
        when(token.getCredentials()).thenReturn(password);

        exception.expect(AuthenticationException.class);
        AuthenticationInfo authInfo = realm.getAuthenticationInfo(token);
        assertEquals(1, authInfo.getPrincipals().asList().size());
    }

    /***
     * Test that a user gets the correct roles.
     * @throws SQLException
     */
    @Test
    public void testGetRolesForUsers() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultset1 = mock(ResultSet.class);
        when(resultset1.next()).thenReturn(true).thenReturn(false);
        when(resultset1.getString(eq("password"))).thenReturn("adEp0s7cQHrJcIsDTDoFQK8eUAIFGh23wew7Klis1sk=");
        when(resultset1.getString(eq("salt"))).thenReturn("78dCvMDECA47YMdtCkgkwQ==");
        when(preparedStatement.executeQuery()).thenReturn(resultset1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        Statement statement = mock(Statement.class);
        ResultSet resultset2 = mock(ResultSet.class);
        // This resultset is iterated twice in the test, return the same thing both times
        when(resultset2.next()).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultset2.getString(eq("username"))).thenReturn("on").thenReturn("kn").thenReturn("on").thenReturn("kn");
        when(statement.executeQuery(eq("select * from administrators_view"))).thenReturn(resultset2);
        when(connection.createStatement()).thenReturn(statement);
        when(dataSource.getConnection()).thenReturn(connection);

        UkelonnRealm realm = new UkelonnRealm();
        realm.setDataSource(dataSource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean jadHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "user");
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
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultset1 = mock(ResultSet.class);
        when(resultset1.next()).thenReturn(true).thenReturn(false);
        when(resultset1.getString(eq("password"))).thenReturn("6VuUrsvVkZfxtKwt4tdCmOdXtXCuIbgWhcURzeRpT/g=");
        when(resultset1.getString(eq("salt"))).thenReturn("snKBcs4FMoGZHQlNY2kz5w==");
        when(preparedStatement.executeQuery()).thenReturn(resultset1);
        when(connection.prepareStatement(eq("select * from users where username=?"))).thenReturn(preparedStatement);
        Statement statement = mock(Statement.class);
        ResultSet resultset2 = mock(ResultSet.class);
        // This resultset is iterated twice in the test, return the same thing both times
        when(resultset2.next()).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultset2.getString(eq("username"))).thenReturn("on").thenReturn("kn").thenReturn("on").thenReturn("kn");
        when(statement.executeQuery(eq("select * from administrators_view"))).thenReturn(resultset2);
        when(connection.createStatement()).thenReturn(statement);
        when(dataSource.getConnection()).thenReturn(connection);

        UkelonnRealm realm = new UkelonnRealm();
        realm.setDataSource(dataSource);
        realm.setCredentialsMatcher(createSha256HashMatcher(1024));
        AuthenticationToken token = new UsernamePasswordToken("on", "ola12".toCharArray());
        AuthenticationInfo authenticationInfoForUser = realm.getAuthenticationInfo(token);

        boolean onHasRoleUser = realm.hasRole(authenticationInfoForUser.getPrincipals(), "user");
        assertTrue(onHasRoleUser);

        boolean onHasRoleAdministrator = realm.hasRole(authenticationInfoForUser.getPrincipals(), "administrator");
        assertTrue(onHasRoleAdministrator);
    }

    private CredentialsMatcher createSha256HashMatcher(int iterations) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
        credentialsMatcher.setStoredCredentialsHexEncoded(false);
        credentialsMatcher.setHashIterations(iterations);
        return credentialsMatcher;
    }

}
