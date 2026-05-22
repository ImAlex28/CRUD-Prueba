package messaging;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import messaging.controller.MessageController;
import messaging.dto.BulkMessageRequest;
import messaging.dto.MessageRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class MessageResourceTest {

    @InjectMock
    MessageController controller;

    @Test
    void enqueueOne_returns202_andId() {
        // Arrange
        MessageRequest req = new MessageRequest();
        req.from = "no-reply@example.com";
        req.to = "user@example.com";
        req.subject = "Hola";
        req.bodyText = "Texto";
        req.bodyHtml = "<p>HTML</p>";

        Mockito.when(controller.enqueue(Mockito.any(MessageRequest.class)))
                .thenReturn(42L);

        // Act + Assert
        given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .post("/messages")
        .then()
            .statusCode(202)
            .contentType(ContentType.JSON)
            .body("ids", hasSize(1))
            .body("ids[0]", equalTo(42));
    }

    @Test
    void enqueueBulk_returns202_andIds() {
        // Arrange
        MessageRequest r1 = new MessageRequest();
        r1.from = "a@ex.com";

        MessageRequest r2 = new MessageRequest();
        r2.from = "b@ex.com";

        BulkMessageRequest bulk = new BulkMessageRequest();
        bulk.messages = List.of(r1, r2);

        Mockito.when(controller.enqueueBulk(Mockito.anyList()))
                .thenReturn(List.of(10L, 11L));

        // Act + Assert
        given()
            .contentType(ContentType.JSON)
            .body(bulk)
        .when()
            .post("/messages/bulk")
        .then()
            .statusCode(202)
            .contentType(ContentType.JSON)
            .body("ids", contains(10, 11));
    }
}