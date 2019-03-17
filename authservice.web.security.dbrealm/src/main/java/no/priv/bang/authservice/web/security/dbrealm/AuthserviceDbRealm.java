package no.priv.bang.authservice.web.security.dbrealm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.authservice.definitions.AuthserviceDatabaseService;

@Component( service=Realm.class, immediate=true )
public class AuthserviceDbRealm extends JdbcRealm {

    LogService logservice;
    private AuthserviceDatabaseService database;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setDatabaseService(AuthserviceDatabaseService database) {
        this.database = database;
    }

    @Activate
    public void activate() {
        dataSource = database.getDatasource();
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("SHA-256");
        credentialsMatcher.setStoredCredentialsHexEncoded(false); // base64 encoding, not hex
        credentialsMatcher.setHashIterations(1024);
        setCredentialsMatcher(credentialsMatcher);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        if (!(token instanceof UsernamePasswordToken)) {
            throw new AuthenticationException("UkelonnRealm shiro realm only accepts UsernamePasswordToken");
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        Object principal = usernamePasswordToken.getPrincipal();
        String username = usernamePasswordToken.getUsername();

        try (PreparedStatement statement = dataSource.getConnection().prepareStatement("select * from users where username=?")) {
            statement.setString(1, username);
            try (ResultSet passwordResultSet = statement.executeQuery()) {
                if (passwordResultSet == null) {
                    throw new AuthenticationException("UkelonnRealm shiro realm failed to get passwords from the database");
                }

                if (passwordResultSet.next()) {
                    String password = passwordResultSet.getString("password");
                    String salt = passwordResultSet.getString("password_salt");
                    ByteSource decodedSalt = Util.bytes(Base64.getDecoder().decode(salt));
                    return new SimpleAuthenticationInfo(principal, password, decodedSalt, getName());
                } else {
                    String message = "Username \"" + username + "\" not found";
                    logservice.log(LogService.LOG_WARNING, message);
                    throw new IncorrectCredentialsException(message);
                }
            }
        } catch (SQLException e) {
            String message = "AuthserviceDbRealm shiro realm got SQL error exploring the password results";
            logservice.log(LogService.LOG_ERROR, message, e);
            throw new AuthenticationException(message, e);
        }
    }

}
