package no.priv.bang.authservice.web.security.dbrealm;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component( service=Realm.class, immediate=true )
public class AuthserviceDbRealm extends JdbcRealm {

    @Override
    @Reference(target = "(osgi.jndi.service.name=jdbc/authservice)")
    public void setDataSource(DataSource datasource) {
        super.setDataSource(datasource);
    }

    @Activate
    public void activate() {
        setSaltStyle(SaltStyle.COLUMN);
        var credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("SHA-256");
        credentialsMatcher.setStoredCredentialsHexEncoded(false); // base64 encoding, not hex
        credentialsMatcher.setHashIterations(1024);
        setCredentialsMatcher(credentialsMatcher);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        if (isLocked(username)) {
            throw new LockedAccountException();
        }

        return super.doGetAuthenticationInfo(token);
    }

    private boolean isLocked(String username) {
        try(var connection = dataSource.getConnection()) {
            try(var statement = connection.prepareStatement("select is_locked from users where username=?")) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getBoolean("is_locked");
                    }
                }
            }
        } catch (SQLException e) { /* eat quietly */}

        return false;
    }

}
