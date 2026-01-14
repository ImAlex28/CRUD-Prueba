
package com.imalex28.crudclientes.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doThrow;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.controller.auth.AuthController;
import com.imalex28.crudclientes.controller.auth.JwtGenerator;
import com.imalex28.crudclientes.dto.auth.CredentialsRequestDTO;
import com.imalex28.crudclientes.dto.auth.LoginResponseDTO;
import com.imalex28.crudclientes.service.auth.AuthService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    JwtGenerator jwtGenerator;

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController controller;

    // ====== /auth/login ======

    @Test
    void userLogin_devuelve_200_y_LoginResponseDTO_con_token() {
        // Arrange
        CredentialsRequestDTO req = new CredentialsRequestDTO();
        req.setUsername("alex28");
        req.setPassword("1234");

        String issuedToken = "jwt-token-123";
        when(authService.loginAndIssueToken("alex28", "1234")).thenReturn(issuedToken);

        // Act
        Response resp = controller.userLogin(req);

        // Assert
        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        assertNotNull(resp.getEntity());
        assertInstanceOf(LoginResponseDTO.class, resp.getEntity());

        LoginResponseDTO body = (LoginResponseDTO) resp.getEntity();
        assertEquals(issuedToken, body.getToken());

        verify(authService, times(1)).loginAndIssueToken("alex28", "1234");
        verifyNoMoreInteractions(authService, jwtGenerator);
    }

    @Test
    void userLogin_si_service_falla_devuelve_500_y_error_en_body() {
        // Arrange
        CredentialsRequestDTO req = new CredentialsRequestDTO();
        req.setUsername("alex28");
        req.setPassword("bad");

        when(authService.loginAndIssueToken("alex28", "bad"))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        Response resp = controller.userLogin(req);

        // Assert
        assertNotNull(resp);
        assertEquals(500, resp.getStatus());
        assertNotNull(resp.getEntity());
        assertInstanceOf(Map.class, resp.getEntity());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getEntity();
        assertEquals(1, body.size());
        assertEquals("Invalid credentials", body.get("error"));

        verify(authService, times(1)).loginAndIssueToken("alex28", "bad");
        verifyNoMoreInteractions(authService, jwtGenerator);
    }

    // ====== /auth/admin/login ======

    @Test
    void adminLogin_devuelve_200_y_token_en_map() throws Exception {
        // Arrange
        String adminToken = "admin-jwt-token";
        when(jwtGenerator.generateToken("alex28", new String[] { "admin" }, 3600))
            .thenReturn(adminToken);

        // Act
        Response resp = controller.adminLogin();

        // Assert
        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        assertNotNull(resp.getEntity());
        assertInstanceOf(Map.class, resp.getEntity());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getEntity();
        assertEquals(1, body.size());
        assertEquals(adminToken, body.get("token"));

        verify(jwtGenerator, times(1)).generateToken("alex28", new String[] { "admin" }, 3600);
        verifyNoMoreInteractions(jwtGenerator, authService);
    }

    @Test
    void adminLogin_si_jwtGenerator_falla_devuelve_500_y_error_en_body() throws Exception {
        // Arrange
        when(jwtGenerator.generateToken("alex28", new String[] { "admin" }, 3600))
            .thenThrow(new RuntimeException("JWT generation error"));

        // Act
        Response resp = controller.adminLogin();

        // Assert
        assertNotNull(resp);
        assertEquals(500, resp.getStatus());
        assertNotNull(resp.getEntity());
        assertInstanceOf(Map.class, resp.getEntity());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getEntity();
        assertEquals(1, body.size());
        assertEquals("JWT generation error", body.get("error"));

        verify(jwtGenerator, times(1)).generateToken("alex28", new String[] { "admin" }, 3600);
        verifyNoMoreInteractions(jwtGenerator, authService);
    }
}
