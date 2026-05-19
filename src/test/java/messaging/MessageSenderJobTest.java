/*package messaging;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import messaging.apiclient.NotifiedEmailClient;
import messaging.model.Message;
import messaging.repository.MessageRepository;
import messaging.worker.MessageSenderJob;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class MessageSenderJobTest {

    @Inject
    MessageSenderJob job;

    @InjectMock
    MessageRepository repo;

    @InjectMock
    @RestClient
    NotifiedEmailClient emailClient;

    // ---------- Helpers ----------
    private static Message newMessage(long id, String to, String subject, String bodyHtml, String bodyText) {
        Message m = new Message();
        setIdReflect(m, id);
        m.setTo(to);
        m.setSubject(subject);
        m.setBodyHtml(bodyHtml);
        m.setBodyText(bodyText);
        return m;
    }

    private static void setIdReflect(Message m, Long id) {
        try {
            Field f = Message.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- Tests ----------
    @Test
    void run_whenNoPending_doesNothing() {
        when(repo.findPending(20)).thenReturn(Collections.emptyList());

        job.run();

        verify(repo, times(1)).findPending(20);
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(emailClient);
    }

    @Test
    void run_whenClaimReturnsZero_skipsMessage() {
        Message m = newMessage(1L, "user@example.com", "Hi", "<b>Hello</b>", "Hello");
        when(repo.findPending(20)).thenReturn(List.of(m));
        when(repo.claimPendingById(eq(1L), any())).thenReturn(0);

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(1L), any());
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(emailClient);
    }

    @Test
    void run_when2xxAndJsonBody_marksSent_and_setsProviderId() {
        Message m = newMessage(2L, "john.doe@example.com", "Subject", "<p>H</p>", "H");
        when(repo.findPending(20)).thenReturn(List.of(m));
        when(repo.claimPendingById(eq(2L), any())).thenReturn(1);


		String json = "{\"id\":\"abcd\"}";
	    RestResponse<String> res = RestResponse.ResponseBuilder.<String>create(200, json)
	        .header("Content-Type", "application/json")
	        .build();


        when(emailClient.sendEmail(eq("john.doe@example.com"), eq("Subject"), eq("<p>H</p>"), eq("H")))
                .thenReturn(res);

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(2L), any());
        verify(emailClient).sendEmail(eq("john.doe@example.com"), eq("Subject"), eq("<p>H</p>"), eq("H"));
        verify(repo).markSent(eq(2L), any(), isNull());
        verify(repo).setProviderMessageId(eq(2L), any());
        verifyNoMoreInteractions(repo);
    }

    @Test
    void run_when2xxAndNonJsonBody_marksSent_and_setsProviderIdNull() {
        Message m = newMessage(3L, "jane@example.com", "S", "<p>x</p>", "x");
        when(repo.findPending(20)).thenReturn(List.of(m));
        when(repo.claimPendingById(eq(3L), any())).thenReturn(1);


		RestResponse<String> res =
		    RestResponse.ResponseBuilder.<String>create(204, "OK, but not JSON").build();

        when(emailClient.sendEmail(eq("jane@example.com"), eq("S"), eq("<p>x</p>"), eq("x")))
                .thenReturn(res);

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(3L), any());
        verify(emailClient).sendEmail(eq("jane@example.com"), eq("S"), eq("<p>x</p>"), eq("x"));
        verify(repo).markSent(eq(3L), any(), isNull());
        verify(repo).setProviderMessageId(3L, null);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void run_whenHttpError_marksFailed() {
        Message m = newMessage(4L, "bad@example.com", "Oops", "<p>y</p>", "y");
        when(repo.findPending(20)).thenReturn(List.of(m));
        when(repo.claimPendingById(eq(4L), any())).thenReturn(1);


		RestResponse<String> res =
		    RestResponse.ResponseBuilder.<String>create(500, "boom").build();

        when(emailClient.sendEmail(eq("bad@example.com"), eq("Oops"), eq("<p>y</p>"), eq("y")))
                .thenReturn(res);

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(4L), any());
        verify(emailClient).sendEmail(eq("bad@example.com"), eq("Oops"), eq("<p>y</p>"), eq("y"));
        verify(repo).markFailed(eq(4L), argThat(msg -> msg != null && msg.contains("Error HTTP 500")));
        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).setProviderMessageId(anyLong(), any());
        verifyNoMoreInteractions(repo);
    }

    @Test
    void run_whenClientThrowsException_marksFailed() {
        Message m = newMessage(5L, "ex@example.com", "X", "<p>z</p>", "z");
        when(repo.findPending(20)).thenReturn(List.of(m));
        when(repo.claimPendingById(eq(5L), any())).thenReturn(1);

        when(emailClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("network down"));

        job.run();

        verify(repo).findPending(20);
        verify(repo).claimPendingById(eq(5L), any());
        verify(emailClient).sendEmail(eq("ex@example.com"), eq("X"), eq("<p>z</p>"), eq("z"));
        verify(repo).markFailed(eq(5L), eq("network down"));
        verify(repo, never()).markSent(anyLong(), any(), any());
        verify(repo, never()).setProviderMessageId(anyLong(), any());
        verifyNoMoreInteractions(repo);
    }
}*/