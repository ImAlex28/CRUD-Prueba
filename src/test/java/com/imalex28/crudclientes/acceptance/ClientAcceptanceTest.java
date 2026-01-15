
package com.imalex28.crudclientes.acceptance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.imalex28.crudclientes.controller.ClienteController;
import com.imalex28.crudclientes.controller.auth.JwtGenerator;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepository;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
@TestHTTPEndpoint(ClienteController.class)
public class ClientAcceptanceTest {

    @InjectMock
    @jakarta.inject.Named("jpa")
    ClienteRepository clientRepository;

    @Inject
    JwtGenerator jwtGenerator;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Reset mocks for test independence.
        Mockito.reset(clientRepository);

        // Generate a valid token for the user
        userToken = jwtGenerator.generateToken("alex28", new String[] {"user"}, 3600);
    }

    @Test
    void list_returns200_withItems() {
        var clientA = new Cliente(); clientA.setIdCliente(1L); clientA.setNombre("Alejandro"); clientA.setEmail("alejandro@example.com");
        var clientB = new Cliente(); clientB.setIdCliente(2L); clientB.setNombre("Maria"); clientB.setEmail("maria@example.com");

        Mockito.when(clientRepository.findAll()).thenReturn(List.of(clientA, clientB));

        given()
            .accept(ContentType.JSON)
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get()
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("", hasSize(2))
            .body("[0].id", equalTo(1))
            .body("[0].nombre", equalTo("Alejandro"))
            .body("[1].id", equalTo(2));
    }

    @Test
    void create_returns409_whenEmailAlreadyExists() {
        var email = "alejandro@example.com";
        var existente = new Cliente();
        existente.setIdCliente(1L);
        existente.setNombre("Alejandro");
        existente.setEmail(email);

        Mockito.when(clientRepository.findByEmail(email)).thenReturn(existente);

        var payload = """
            { "nombre": "Otro", "email": "alejandro@example.com" }
        """;

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + userToken)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(409)
            .contentType(ContentType.JSON)
            .body("message", org.hamcrest.Matchers.containsString("Ya existe un cliente con el email"));
    }
}
