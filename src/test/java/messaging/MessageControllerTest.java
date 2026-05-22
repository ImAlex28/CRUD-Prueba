package messaging;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import messaging.controller.MessageController;
import messaging.dto.MessageRequest;
import messaging.model.Message;
import messaging.model.SendStatus;
import messaging.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@QuarkusTest
class MessageControllerTest {

    @Inject
    MessageController controller;

    @InjectMock
    MessageRepository repository;

    @Test
    void enqueue_persists_and_returns_id() {
        // Arrange
        MessageRequest dto = new MessageRequest();
        dto.from = "no-reply@example.com";
        dto.subject = "Asunto";

        doAnswer(inv -> {
            Message m = inv.getArgument(0);
            setId(m, 100L);
            return m;
        }).when(repository).save(any(Message.class));

        // Act
        Long id = controller.enqueue(dto);

        // Assert (JUnit)
        assertEquals(100L, id);

        ArgumentCaptor<Message> cap = ArgumentCaptor.forClass(Message.class);
        verify(repository).save(cap.capture());
        Message persisted = cap.getValue();
        assertEquals("no-reply@example.com", persisted.getFrom());
        assertEquals("Asunto", persisted.getSubject());
        assertEquals(SendStatus.PENDING, persisted.getSendStatus());
    }

    @Test
    void enqueueBulk_calls_enqueue_for_each_and_collects_ids() {
        // Arrange
        MessageRequest a = new MessageRequest(); a.from = "a@ex.com";
        MessageRequest b = new MessageRequest(); b.from = "b@ex.com";

        when(repository.save(any(Message.class)))
            .thenAnswer(inv -> {
                Message m = inv.getArgument(0);
                // simula autoincrement
                Long nextId = (m.getId() == null) ? 1L : m.getId() + 1;
                setId(m, nextId);
                return m;
            });

        // Act
        List<Long> ids = controller.enqueueBulk(List.of(a, b));

        // Assert (JUnit)
        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertNotNull(ids.get(0));
        assertNotNull(ids.get(1));
        verify(repository, times(2)).save(any(Message.class));
    }

    /** Helper para setear el id si es generado. */
    private static void setId(Message m, Long id) {
        try {
            var f = Message.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}