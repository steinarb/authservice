package no.priv.bang.authservice.web.security.dbrealm;

import javax.sql.DataSource;

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

}
