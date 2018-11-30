package no.priv.bang.authservice;

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
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;

public class UkelonnRealm extends AuthorizingRealm {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        Set<String> administrators = new HashSet<String>();
        try {
            Statement statement = dataSource.getConnection().createStatement();
            ResultSet administratorsResults = statement.executeQuery("select * from administrators_view");
            while (administratorsResults.next()) {
                administrators.add(administratorsResults.getString("username"));
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

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo(roles);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!(token instanceof UsernamePasswordToken)) {
            throw new AuthenticationException("UkelonnRealm shiro realm only accepts UsernamePasswordToken");
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        Object principal = usernamePasswordToken.getPrincipal();
        String username = usernamePasswordToken.getUsername();

        try {
            PreparedStatement statement = dataSource.getConnection().prepareStatement("select * from users where username=?");
            statement.setString(1, username);
            ResultSet passwordResultSet = statement.executeQuery();
            if (passwordResultSet == null) {
                throw new AuthenticationException("UkelonnRealm shiro realm failed to get passwords from the database");
            }

            if (passwordResultSet.next()) {
                String password = passwordResultSet.getString("password");
                String salt = passwordResultSet.getString("salt");
                ByteSource decodedSalt = Util.bytes(Base64.getDecoder().decode(salt));
                return new SimpleAuthenticationInfo(principal, password, decodedSalt, getName());
            } else {
                throw new IncorrectCredentialsException("Username \"" + username + "\" not found");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("UkelonnRealm shiro realm got SQL error exploring the password results", e);
        }
    }

}
