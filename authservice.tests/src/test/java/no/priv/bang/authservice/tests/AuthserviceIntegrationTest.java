package no.priv.bang.authservice.tests;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.OptionUtils.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.Servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.container.internal.JavaVersionUtil;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AuthserviceIntegrationTest {
    public static final String RMI_SERVER_PORT = "44445";
    public static final String RMI_REG_PORT = "1100";

    @Inject
    private Servlet authserviceServlet;

    @Configuration
    public Option[] config() {
        final String jmxPort = freePortAsString();
        final String httpPort = freePortAsString();
        final String httpsPort = freePortAsString();
        final MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf-minimal").type("zip").versionAsInProject();
        final MavenArtifactUrlReference paxJdbcRepo = maven().groupId("org.ops4j.pax.jdbc").artifactId("pax-jdbc-features").versionAsInProject().type("xml").classifier("features");
        final MavenArtifactUrlReference authserviceFeatureRepo = maven().groupId("no.priv.bang.authservice").artifactId("authservice").version("LATEST").type("xml").classifier("features");
        Option[] commonOptions = options(
            karafDistributionConfiguration().frameworkUrl(karafUrl).unpackDirectory(new File("target/exam")).useDeployFolder(false).runEmbedded(true),
            configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
            systemTimeout(720000),
            keepRuntimeFolder(),
            logLevel(LogLevel.DEBUG),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT),
            editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", httpPort),
            editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port.secure", httpsPort),
            replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getConfigFile("/etc/org.ops4j.pax.logging.cfg")),
            systemProperty("org.ops4j.pax.logging.DefaultSer‌​viceLog.level").value("DEBUG"),
            vmOptions("-Dtest-jmx-port=" + jmxPort),
            junitBundles(),
            features(paxJdbcRepo),
            features(authserviceFeatureRepo, "user-admin-with-derby"));
        if (JavaVersionUtil.getMajorVersion() < 9) {
            return commonOptions;
        }

        Option[] java11VmOptions = createJava11VmOptions();
        return combine(commonOptions, java11VmOptions);
    }

    private Option[] createJava11VmOptions() {
        return options(
            vmOption("--add-reads=java.xml=java.logging"),
            vmOption("--add-exports=java.base/org.apache.karaf.specs.locator=java.xml,ALL-UNNAMED"),
            vmOption("--patch-module"), vmOption("java.base=lib/endorsed/org.apache.karaf.specs.locator-" + System.getProperty("karaf.version") + ".jar"),
            vmOption("--patch-module"), vmOption("java.xml=lib/endorsed/org.apache.karaf.specs.java.xml-" + System.getProperty("karaf.version") + ".jar"),
            vmOption("--add-opens"), vmOption("java.base/java.security=ALL-UNNAMED"),
            vmOption("--add-opens"), vmOption("java.base/java.net=ALL-UNNAMED"),
            vmOption("--add-opens"), vmOption("java.base/java.lang=ALL-UNNAMED"),
            vmOption("--add-opens"), vmOption("java.base/java.util=ALL-UNNAMED"),
            vmOption("--add-opens"), vmOption("java.naming/javax.naming.spi=ALL-UNNAMED"),
            vmOption("--add-opens"), vmOption("java.rmi/sun.rmi.transport.tcp=ALL-UNNAMED"),
            vmOption("--add-exports=java.base/sun.net.www.protocol.http=ALL-UNNAMED"),
            vmOption("--add-exports=java.base/sun.net.www.protocol.https=ALL-UNNAMED"),
            vmOption("--add-exports=java.base/sun.net.www.protocol.jar=ALL-UNNAMED"),
            vmOption("--add-exports=jdk.naming.rmi/com.sun.jndi.url.rmi=ALL-UNNAMED"),
            vmOption("-classpath"), vmOption("lib/jdk9plus/*" + File.pathSeparator + "lib/boot/*"));
    }

    @Test
    public void testAuthserviceServlet() {
        // Verify that the service could be injected
        assertNotNull(authserviceServlet);
    }

    static int freePort() {
        try (final ServerSocket serverSocket = new ServerSocket(0)) {
            serverSocket.setReuseAddress(true);
            final int port = serverSocket.getLocalPort();

            return port;
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static String freePortAsString() {
        return Integer.toString(freePort());
    }

    public File getConfigFile(String path) {
        URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("Config resource " + path + " not found");
        }
        return new File(res.getFile());
    }

}
