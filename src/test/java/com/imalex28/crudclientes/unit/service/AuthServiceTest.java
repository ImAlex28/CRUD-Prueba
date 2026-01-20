
package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.controller.auth.JwtGenerator;
import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.repository.ClientRepository;
import com.imalex28.crudclientes.service.auth.AuthServiceImpl;
import com.imalex28.crudclientes.service.auth.InvalidCredentialsException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    JwtGenerator jwtGenerator;

    @InjectMocks
    AuthServiceImpl service;

    // ---------- login (OK) ----------
    @Test
    void loginAndIssueToken_ok_devuelveToken() throws Exception {
        String email = "user@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);
        when(jwtGenerator.generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong()))
                .thenReturn("token-abc");

        String token = service.loginAndIssueToken(email, "pwd");

        assertNotNull(token);
        assertEquals("token-abc", token);

        verify(clientRepository, times(1)).findByEmail(email);
        verify(jwtGenerator, times(1)).generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }

    // ---------- login (cache reuse) ----------
    @Test
    void loginAndIssueToken_reutilizaCache_noRegeneraTokenSiNoExpira() throws Exception {
        String email = "cache@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);

        // Simulamos que la primera generación devuelve token-1; la segunda llamada,
        // si la caché funciona, debe retornar el mismo token-1 sin invocar jwtGenerator de nuevo.
        AtomicInteger signCount = new AtomicInteger();
        when(jwtGenerator.generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong()))
                .thenAnswer(inv -> {
                    signCount.incrementAndGet();
                    return "token-1";
                });

        String t1 = service.loginAndIssueToken(email, "pwd");
        String t2 = service.loginAndIssueToken(email, "pwd");

        assertEquals("token-1", t1);
        assertEquals("token-1", t2);
        assertEquals(1, signCount.get(), "jwtGenerator debe haberse invocado sólo una vez");

        verify(clientRepository, times(2)).findByEmail(email);
        verify(jwtGenerator, times(1)).generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }

    // ---------- login (KO: usuario no existe) ----------
    @Test
    void loginAndIssueToken_ko_usuarioNoEncontrado_lanzaInvalidCredentials() {
        String username = "missing@example.com";
        when(clientRepository.findByEmail(username)).thenReturn(null);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> service.loginAndIssueToken(username, "pwd"));

        assertNotNull(ex);
        verify(clientRepository, times(1)).findByEmail(username);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(jwtGenerator);
    }

    // ---------- login (KO: password nula) ----------
    @Test
    void loginAndIssueToken_ko_passwordNull_lanzaInvalidCredentials() {
        String email = "user@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> service.loginAndIssueToken(email, null));

        assertNotNull(ex);
        verify(clientRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(clientRepository);
        verifyNoInteractions(jwtGenerator);
    }

    // ---------- logout (invalida caché) ----------
    @Test
    void logout_invalidaToken_yFuerzaNuevaFirma() throws Exception {
        String email = "logout@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);
        when(jwtGenerator.generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong()))
                .thenReturn("t1", "t2");

        // Primera llamada: genera t1 y lo cachea
        String first = service.loginAndIssueToken(email, "pwd");
        assertEquals("t1", first);

        // Logout invalida la caché
        service.logout(email);

        // Siguiente login debe generar un token nuevo (t2)
        String second = service.loginAndIssueToken(email, "pwd");
        assertEquals("t2", second);

        verify(clientRepository, times(2)).findByEmail(email);
        verify(jwtGenerator, times(2)).generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }

    // ---------- invalidateUserToken (invalida caché) ----------
    @Test
    void invalidateUserToken_invalidaCache_yFuerzaNuevaFirma() throws Exception {
        String email = "invalidate@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);
        when(jwtGenerator.generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong()))
                .thenReturn("t1", "t2");

        String first = service.loginAndIssueToken(email, "pwd");
        assertEquals("t1", first);

        service.invalidateUserToken(email);

        String second = service.loginAndIssueToken(email, "pwd");
        assertEquals("t2", second);

        verify(clientRepository, times(2)).findByEmail(email);
        verify(jwtGenerator, times(2)).generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }

    // ---------- clearAllTokens (limpia caché) ----------
    @Test
    void clearAllTokens_limpiaCache_yFuerzaNuevaFirma() throws Exception {
        String email = "clear@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);
        when(jwtGenerator.generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong()))
                .thenReturn("t1", "t2");

        String first = service.loginAndIssueToken(email, "pwd");
        assertEquals("t1", first);

        service.clearAllTokens();

        String second = service.loginAndIssueToken(email, "pwd");
        assertEquals("t2", second);

        verify(clientRepository, times(2)).findByEmail(email);
        verify(jwtGenerator, times(2)).generateToken(eq(email), ArgumentMatchers.<String[]>any(), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }
    
    @Test
    void loginAndIssueToken_cuandoJwtGeneratorFalla_lanzaRuntimeExceptionConMensajeEsperado() throws Exception {
        // Arrange: usuario válido sin token en caché
        String email = "user@example.com";
        Client cliente = new Client();
        cliente.setEmail(email);

        when(clientRepository.findByEmail(email)).thenReturn(cliente);

        // Provocar fallo en la firma JWT → esto activará el catch de generateJwtForUser(...)
        when(jwtGenerator.generateToken(eq(email), any(String[].class), anyLong()))
            .thenThrow(new Exception("firma RSA fallida"));

        // Act & Assert: el servicio debe envolver la excepción en RuntimeException con el mensaje esperado
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> service.loginAndIssueToken(email, "pwd"));

        assertEquals("Error generating token", ex.getMessage());
        // También puedes validar que la causa es la Exception original
        assertEquals(Exception.class, ex.getCause().getClass());
        assertEquals("firma RSA fallida", ex.getCause().getMessage());

        // Verificaciones de interacción
        verify(clientRepository, times(1)).findByEmail(email);
        verify(jwtGenerator, times(1)).generateToken(eq(email), any(String[].class), anyLong());
        verifyNoMoreInteractions(clientRepository, jwtGenerator);
    }
}

