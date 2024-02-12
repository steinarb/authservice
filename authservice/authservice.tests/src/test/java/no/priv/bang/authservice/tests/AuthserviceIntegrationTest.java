package no.priv.bang.authservice.tests;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.util.stream.Stream;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AuthserviceIntegrationTest extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        final MavenArtifactUrlReference authserviceFeatureRepo = maven()
            .groupId("no.priv.bang.authservice")
            .artifactId("karaf")
            .versionAsInProject()
            .type("xml")
            .classifier("features");
        Option[] options = new Option[] {
            features(authserviceFeatureRepo)
        };
        return Stream.of(super.config(), options).flatMap(Stream::of).toArray(Option[]::new);
    }

    @Test
    public void testLoadFeature() throws Exception { // NOSONAR this test has an assert, just not an assert sonar recognizes
        installAndAssertFeature("user-admin-with-testdb");
    }

}
