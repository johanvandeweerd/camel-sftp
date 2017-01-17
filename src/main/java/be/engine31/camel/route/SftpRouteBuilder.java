package be.engine31.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SftpRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("sftp:demo@test.rebex.net:22/pub/example?password=password&knownHostsUri=known_hosts&useUserKnownHostsFile=false")
                .log("${file:name}");
    }
}
