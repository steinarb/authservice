package no.priv.bang.authservice.web.security.dbrealm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import no.priv.bang.osgi.service.database.DatabaseService;

@Component( service=Realm.class, immediate=true )
public class AuthserviceDbRealm extends AuthorizingRealm {

    LogService logservice;
    private DatabaseService database;
    private DataSource dataSource;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logservice = logservice;
    }

    @Reference
    public void setDatabaseService(DatabaseService database) {
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
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        Set<String> administrators = new HashSet<>();
        try (Statement statement = dataSource.getConnection().createStatement()) {
            try (ResultSet administratorsResults = statement.executeQuery("select * from administrators_view")) {
                while (administratorsResults.next()) {
                    administrators.add(administratorsResults.getString("username"));
                }
            }
        } catch (Exception e) {
            throw new AuthorizationException(e);
        }

        Collection<String> usernames = principals.byType(String.class);
        boolean allPrincipalsAreAdministrators = true;
        for (String username : usernames) {
            allPrincipalsAreAdministrators &= administrators.contains(username);
        }

        if (allPrincipalsAreAdministrators) {
            roles.add("administrator");
        }

        return new SimpleAuthorizationInfo(roles);
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
