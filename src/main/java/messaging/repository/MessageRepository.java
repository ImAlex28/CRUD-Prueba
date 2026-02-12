package messaging.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import messaging.model.Message;
import messaging.model.SendStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MessageRepository {

    @Inject
    EntityManager em;

    @Transactional
    public Message save(Message message) {
        if (message.getId() == null) {
            em.persist(message);
            return message;
        }
        return em.merge(message);
    }

    public Optional<Message> findById(Long id) {
        return Optional.ofNullable(em.find(Message.class, id));
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    public List<Message> findPending(int limit) {
        return em.createQuery("""
                select m from Message m
                where m.sendStatus = :status
                order by m.createdAt asc
                """, Message.class)
            .setParameter("status", SendStatus.PENDING)
            .setMaxResults(limit)
            .getResultList();
    }

    public List<Message> findRetryable(int maxAttempts, int limit) {
        return em.createQuery("""
                select m from Message m
                where m.sendStatus = :status
                  and m.attempts < :maxAttempts
                order by m.createdAt asc
                """, Message.class)
            .setParameter("status", SendStatus.FAILED)
            .setParameter("maxAttempts", maxAttempts)
            .setMaxResults(limit)
            .getResultList();
    }

    public long countByStatus(SendStatus status) {
        return em.createQuery("""
                select count(m) from Message m
                where m.sendStatus = :status
                """, Long.class)
            .setParameter("status", status)
            .getSingleResult();
    }

    public Optional<Message> findByProviderMessageId(String providerMessageId) {
        List<Message> result = em.createQuery("""
                select m from Message m
                where m.providerMessageId = :pid
                """, Message.class)
            .setParameter("pid", providerMessageId)
            .setMaxResults(1)
            .getResultList();

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Message> findSentBefore(Instant before, int limit) {
        return em.createQuery("""
                select m from Message m
                where m.sendStatus = :status
                  and m.sentAt < :before
                order by m.sentAt asc
                """, Message.class)
            .setParameter("status", SendStatus.SENT)
            .setParameter("before", before)
            .setMaxResults(limit)
            .getResultList();
    }

    @Transactional
    public int claimPendingById(long id, Instant now) {
        return em.createQuery("""
                update Message m
                set m.sendStatus = :sending,
                    m.lastAttemptAt = :now,
                    m.attempts = m.attempts + 1
                where m.id = :id
                  and m.sendStatus = :pending
                """)
            .setParameter("sending", SendStatus.SENDING)
            .setParameter("now", now)
            .setParameter("id", id)
            .setParameter("pending", SendStatus.PENDING)
            .executeUpdate();
    }

    @Transactional
    public int markSent(long id, Instant sentAt, String providerMessageId) {
        return em.createQuery("""
                update Message m
                set m.sendStatus = :sent,
                    m.sentAt = :sentAt,
                    m.providerMessageId = :pid,
                    m.lastError = null
                where m.id = :id
                """)
            .setParameter("sent", SendStatus.SENT)
            .setParameter("sentAt", sentAt != null ? sentAt : Instant.now())
            .setParameter("pid", providerMessageId)
            .setParameter("id", id)
            .executeUpdate();
    }

    @Transactional
    public int markFailed(long id, String error) {
        String safeError = error == null ? null : (error.length() > 4000 ? error.substring(0, 4000) : error);

        return em.createQuery("""
                update Message m
                set m.sendStatus = :failed,
                    m.lastError = :err
                where m.id = :id
                """)
            .setParameter("failed", SendStatus.FAILED)
            .setParameter("err", safeError)
            .setParameter("id", id)
            .executeUpdate();
    }
    
    @Transactional
    public int setProviderMessageId(long id, String providerMessageId) {
    	return em.createQuery("""
                update Message m
                set m.providerMessageId = :pid
                where m.id = :id
                """)
    			.setParameter("pid", providerMessageId)
    			.setParameter("id",id)
    			.executeUpdate();
    }
}