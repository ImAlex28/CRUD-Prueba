package h2;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.h2.tools.Server;

@Singleton
@Startup
public class H2TcpServer {

    private Server server;

    @PostConstruct
    void start() throws Exception {
        server = Server.createTcpServer(
                "-tcp",
                "-tcpAllowOthers",
                "-ifNotExists"
        ).start();
    }

    @PreDestroy
    void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
