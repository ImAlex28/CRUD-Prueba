package messaging;

import com.mailersend.sdk.MailerSendResponse;
import messaging.apiclient.MailerSendClient;
import messaging.repository.MessageRepository;
import messaging.worker.MessageSenderJob2;
import messaging.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSenderJob2Test {

    @Mock
    MessageRepository repo;

    @Mock
    MailerSendClient emailClient;

    MessageSenderJob2 job;

    @BeforeEach
    void setUp() {
        job = new MessageSenderJob2();
        job.repo = repo;
        job.emailClient = emailClient;
    }

    @Test
    void run_noPending_doesNothing() {
        when(repo.findPending(20)).thenReturn(List.of());

        job.run();

        verify(repo).findPending(20);
        verifyNoMoreInteractions(emailClient);
        verify(repo, never()).claimPendingById(anyLong(), any());
        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).markFailed(anyLong(), anyString());
        verify(repo, never()).setProviderMessageId(anyLong(), anyString());
    }

    @Test
    void run_claimedByAnotherWorker_skipsSend() {
        long id = 42L;
        Message msg = mock(Message.class);
        when(msg.getId()).thenReturn(id);

        when(repo.findPending(20)).thenReturn(List.of(msg));
        when(repo.claimPendingById(eq(id), any(Instant.class))).thenReturn(0);

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(id), any(Instant.class));
        verifyNoInteractions(emailClient);
        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).markFailed(anyLong(), anyString());
    }

    @Test
    void run_success2xx_marksSentAndStoresMessageId() throws Exception {
        long id = 100L;
        Message msg = mockMessage(id, "from@domain.com", "to@domain.com",
                "OK", "Hi", "<p>Hi</p>");
        when(repo.findPending(20)).thenReturn(List.of(msg));
        when(repo.claimPendingById(eq(id), any(Instant.class))).thenReturn(1);

        MailerSendResponse ok = response(202, "msg-123");
        when(emailClient.sendEmail(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(ok);

        job.run();

        verify(repo).claimPendingById(eq(id), any(Instant.class));
        verify(emailClient).sendEmail(eq("from@domain.com"), eq("to@domain.com"),
                eq("OK"), eq("Hi"), eq("<p>Hi</p>"));

        verify(repo).markSent(eq(id), any(Instant.class), isNull());
        verify(repo).setProviderMessageId(eq(id), eq("msg-123"));
        verify(repo, never()).markFailed(anyLong(), anyString());
    }

    @Test
    void run_non2xx_marksFailed() throws Exception {
        long id = 101L;
        Message msg = mockMessage(id, "from@domain.com", "to@domain.com",
                "Bad", "Hi", "<p>Hi</p>");
        when(repo.findPending(20)).thenReturn(List.of(msg));
        when(repo.claimPendingById(eq(id), any(Instant.class))).thenReturn(1);

        MailerSendResponse bad = response(400, "msg-400");
        when(emailClient.sendEmail(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(bad);

        job.run();

        verify(repo).claimPendingById(eq(id), any(Instant.class));
        verify(repo).markFailed(eq(id), contains("400"));

        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).setProviderMessageId(anyLong(), anyString());
    }

    @Test
    void run_exceptionFromClient_marksFailed() throws Exception {
        long id = 102L;
        Message msg = mockMessage(id, "from@domain.com", "to@domain.com",
                "Boom", "Hi", "<p>Hi</p>");
        when(repo.findPending(20)).thenReturn(List.of(msg));
        when(repo.claimPendingById(eq(id), any(Instant.class))).thenReturn(1);

        RuntimeException boom = new RuntimeException("network down");
        when(emailClient.sendEmail(anyString(), anyString(), anyString(), any(), any()))
                .thenThrow(boom);

        job.run();

        verify(repo).claimPendingById(eq(id), any(Instant.class));
        verify(repo).markFailed(eq(id), contains("network down"));

        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).setProviderMessageId(anyLong(), anyString());
    }

    private static Message mockMessage(long id, String from, String to,
                                       String subject, String text, String html) {
        Message m = mock(Message.class);
        when(m.getId()).thenReturn(id);
        when(m.getFrom()).thenReturn(from);
        when(m.getTo()).thenReturn(to);
        when(m.getSubject()).thenReturn(subject);
        when(m.getBodyText()).thenReturn(text);
        when(m.getBodyHtml()).thenReturn(html);
        return m;
    }

    private static MailerSendResponse response(int status, String messageId) throws Exception {
        MailerSendResponse r = new MailerSendResponse();

        setField(r, "responseStatusCode", status);
        setField(r, "messageId", messageId);

        return r;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}