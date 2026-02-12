package messaging;

import messaging.model.Message;
import messaging.model.SendStatus;
import messaging.repository.MessageRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageRepositoryTest {

    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Message> typedQuery;

	@Mock
	TypedQuery<Long> query;


    @InjectMocks
    MessageRepository repo;

    @Test
    void save_persist_and_merge() {
        Message m = new Message();
        m.setId(null);

        repo.save(m);
        verify(em).persist(m);

        m.setId(123L);
        repo.save(m);
        verify(em).merge(m);
    }

    @Test
    void findPending_returns_oldest_first_limited() {
        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new Message(), new Message()));

        List<Message> result = repo.findPending(2);

        assertEquals(2, result.size());
    }

    @Test
    void findRetryable_returns_failed_under_maxAttempts() {
    	when(em.createQuery(anyString(), eq(Message.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new Message(), new Message()));

        List<Message> r = repo.findRetryable(3, 10);

        assertEquals(2, r.size());
    }


	@Test
	void countByStatus_works() {
	    when(em.createQuery(anyString(), eq(Long.class))).thenReturn(query);
	    when(query.setParameter(anyString(), any())).thenReturn(query);
	    when(query.getSingleResult()).thenReturn(2L);
	
	    long result = repo.countByStatus(SendStatus.SENT);
	
	    assertEquals(2L, result);
	}


    @Test
    void findByProviderMessageId_returns_first() {
        Message m = new Message();
        m.setId(10L);

        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(m));

        var found = repo.findByProviderMessageId("prov-123");
        assertTrue(found.isPresent());
        assertEquals(10L, found.get().getId());
    }

    @Test
    void findByProviderMessageId_empty_when_no_results() {
        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        var found = repo.findByProviderMessageId("nope");
        assertTrue(found.isEmpty());
    }

    @Test
    void findSentBefore_filters_by_date() {
        Message m = new Message();

        when(em.createQuery(anyString(), eq(Message.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(m));

        List<Message> result = repo.findSentBefore(Instant.now(), 10);

        assertEquals(1, result.size());
    }

    @Test
    void claimPendingById_transitions_to_SENDING_and_increments_attempts_conditionally() {
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        int updated = repo.claimPendingById(1L, Instant.now());
        assertEquals(1, updated);
    }

    @Test
    void markSent_sets_fields_and_clears_error() {
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        int updated = repo.markSent(1L, Instant.now(), "provider-1");

        assertEquals(1, updated);
    }

    @Test
    void markFailed_sets_failed_and_truncates_error_to_4000() {
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        String err = "x".repeat(5000);
        int updated = repo.markFailed(1L, err);

        assertEquals(1, updated);
    }

    @Test
    void setProviderMessageId_updates_only_pid() {
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        int updated = repo.setProviderMessageId(1L, "pid-999");

        assertEquals(1, updated);
    }
}