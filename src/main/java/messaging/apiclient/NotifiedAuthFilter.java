// src/main/java/com/example/notified/client/NotifiedAuthFilter.java
package messaging.apiclient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@ApplicationScoped
public class NotifiedAuthFilter implements ClientRequestFilter {

    @Inject
    @ConfigProperty(name = "notified.api-key")
    String apiKey;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().putSingle("Authorization", "Api-Key " + apiKey);
    }
}