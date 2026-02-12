package messaging;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import messaging.apiclient.NotifiedAuthFilter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
class NotifiedAuthFilterTest {

    @Inject
    NotifiedAuthFilter filter;

    @Test
    void usesConfiguredApiKeyFromConfigProperty() throws Exception {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        ClientRequestContext ctx = mock(ClientRequestContext.class);
        when(ctx.getHeaders()).thenReturn(headers);

        filter.filter(ctx);

        assertEquals("Api-Key test-key-123", headers.getFirst("Authorization"));
        verify(ctx, times(1)).getHeaders();
        verifyNoMoreInteractions(ctx);
    }
}