
package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.apiclient.IPApiClient;
import com.imalex28.crudclientes.dto.ip.ExternalIPDTO;
import com.imalex28.crudclientes.dto.ip.IPResponseDTO;
import com.imalex28.crudclientes.service.ip.IpCacheService;

@ExtendWith(MockitoExtension.class)
public class IpCacheServiceTest {

    @Mock
    IPApiClient client;

    @InjectMocks
    IpCacheService service;

    // ---------- getIP (OK: cache hit) ----------
    @Test
    void getIP_hitDentroTTL_noLlamaClienteEnSegundaInvocacion() {
        // TTL largo para asegurar que la entrada no expira entre llamadas.
        service.setTtlMillis(100_000L);

        String ip = "8.8.8.8";
        String token = "tk";

        ExternalIPDTO ext = new ExternalIPDTO();
        ext.setIp(ip);
        ext.setCountry("US");
        ext.setAs_name("Google LLC");
        ext.setAs_domain("google.com");

        when(client.getIP(ip, token)).thenReturn(ext);

        // Primera llamada → MISS (llama client.getIP y guarda en caché)
        IPResponseDTO r1 = service.getIP(ip, token);

        assertNotNull(r1);
        assertEquals(ip, r1.getIp());
        assertEquals("US", r1.getCountry());
        assertEquals("Google LLC", r1.getAs_name());
        assertEquals("google.com", r1.getAs_domain());

        // Segunda llamada → HIT (no debería llamar al cliente)
        IPResponseDTO r2 = service.getIP(ip, token);

        assertNotNull(r2);
        // Es la misma información cacheada
        assertEquals(r1.getIp(), r2.getIp());
        assertEquals(r1.getCountry(), r2.getCountry());
        assertEquals(r1.getAs_name(), r2.getAs_name());
        assertEquals(r1.getAs_domain(), r2.getAs_domain());

        verify(client, times(1)).getIP(ip, token);
        verifyNoMoreInteractions(client);
    }

    // ---------- getIP (OK: cache miss por expiración inmediata) ----------
    @Test
    void getIP_expirado_porTTL_cero_vuelveALlamarCliente() {
        // TTL = 0 → cualquier entrada expira inmediatamente
        service.setTtlMillis(0L);

        String ip = "1.1.1.1";
        String token = "tk2";

        ExternalIPDTO ext1 = new ExternalIPDTO();
        ext1.setIp(ip);
        ext1.setCountry("AU");
        ext1.setAs_name("First AS");
        ext1.setAs_domain("first.com");

        ExternalIPDTO ext2 = new ExternalIPDTO();
        ext2.setIp(ip);
        ext2.setCountry("AU");
        ext2.setAs_name("Second AS");
        ext2.setAs_domain("second.com");

        when(client.getIP(ip, token)).thenReturn(ext1, ext2);

        // Primera llamada → MISS
        IPResponseDTO r1 = service.getIP(ip, token);
        assertNotNull(r1);
        assertEquals("First AS", r1.getAs_name());

        // Segunda llamada → como TTL=0, MISS de nuevo, debe refrescar
        IPResponseDTO r2 = service.getIP(ip, token);
        assertNotNull(r2);
        assertEquals("Second AS", r2.getAs_name());

        verify(client, times(2)).getIP(ip, token);
        verifyNoMoreInteractions(client);
    }

    // ---------- invalidateAll ----------
    @Test
    void invalidateAll_limpiaCache_yFuerzaNuevaLlamada() {
        service.setTtlMillis(100_000L);

        String ip = "9.9.9.9";
        String token = "tkn";

        ExternalIPDTO ext1 = new ExternalIPDTO();
        ext1.setIp(ip);
        ext1.setCountry("DE");
        ext1.setAs_name("AS1");
        ext1.setAs_domain("as1.de");

        ExternalIPDTO ext2 = new ExternalIPDTO();
        ext2.setIp(ip);
        ext2.setCountry("DE");
        ext2.setAs_name("AS2");
        ext2.setAs_domain("as2.de");

        when(client.getIP(ip, token)).thenReturn(ext1, ext2);

        // Primer getIP cachea
        IPResponseDTO first = service.getIP(ip, token);
        assertEquals("AS1", first.getAs_name());

        // Invalidamos caché
        service.invalidateAll();

        // Siguiente getIP debe llamar al cliente otra vez
        IPResponseDTO second = service.getIP(ip, token);
        assertEquals("AS2", second.getAs_name());

        verify(client, times(2)).getIP(ip, token);
        verifyNoMoreInteractions(client);
    }

    // ---------- argumentos inválidos ----------
    @Test
    void getIP_ipNull_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getIP(null, "tkn"));
        verifyNoInteractions(client);
    }

    @Test
    void getIP_ipBlank_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.getIP("   ", "tkn"));
        verifyNoInteractions(client);
    }

    // ---------- mapeo de campos desde ExternalIPDTO ----------
    @Test
    void getIP_mapeaCamposCorrectamente_desdeExternalIPDTO() {
        service.setTtlMillis(100_000L);

        String ip = "4.4.4.4";
        String token = "tkn3";

        ExternalIPDTO ext = new ExternalIPDTO();
        ext.setIp(ip);
        ext.setCountry("GB");
        ext.setAs_name("Example AS");
        ext.setAs_domain("example.org");

        when(client.getIP(ip, token)).thenReturn(ext);

        IPResponseDTO resp = service.getIP(ip, token);

        assertNotNull(resp);
        assertEquals("4.4.4.4", resp.getIp());
        assertEquals("GB", resp.getCountry());
        assertEquals("Example AS", resp.getAs_name());
        assertEquals("example.org", resp.getAs_domain());

        verify(client, times(1)).getIP(ip, token);
        verifyNoMoreInteractions(client);
    }
}
