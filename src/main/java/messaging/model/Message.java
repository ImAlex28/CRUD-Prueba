package messaging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_address", nullable = false, length = 320)
    private String from;


    @Column(name = "to_address", nullable = false, length = 320)
    private String to;


    @Column(nullable = false, length = 998)
    private String subject;

    @Lob
    @Column(name = "body_text")
    private String bodyText;

    @Lob
    @Column(name = "body_html")
    private String bodyHtml;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", nullable = false, length = 30)
    private SendStatus sendStatus = SendStatus.PENDING;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "last_error", length = 4000)
    private String lastError;

    @Column(name = "provider_message_id", length = 255)
    private String providerMessageId;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (sendStatus == null) sendStatus = SendStatus.PENDING;
    }

    public void markSending() {
        this.sendStatus = SendStatus.SENDING;
        this.lastAttemptAt = Instant.now();
        this.attempts++;
    }

    public void markSent(String providerMessageId) {
        this.sendStatus = SendStatus.SENT;
        this.sentAt = Instant.now();
        this.providerMessageId = providerMessageId;
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.sendStatus = SendStatus.FAILED;
        this.lastError = error;
    }

}

