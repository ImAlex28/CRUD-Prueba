package messaging.worker;

import java.time.Instant;
import java.util.List;

import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.inject.Inject;
import messaging.apiclient.MailerSendClient;
import messaging.model.Message;
import messaging.repository.MessageRepository;

public class MessageSenderJob2 {

    @Inject
	public
    MessageRepository repo;

    @Inject
	public
    MailerSendClient emailClient;
    

	private static final Logger log = LoggerFactory.getLogger(MessageSenderJob2.class);


    @Scheduled(identity = "message-sender-job", every = "{messaging.sender.interval:60s}")
	public void run() {
        List<Message> pending = repo.findPending(20);

        log.info("[Worker] Encontrados {} mensajes PENDING", pending.size());


        for (Message message : pending) {
            long id = message.getId();
            log.info("[Worker] Procesando id={} to={} subject='{}'", id, safeEmail(message.getTo()), trim(message.getSubject(), 120));
            
            int updated = repo.claimPendingById(id, Instant.now());
            if (updated == 0) {
                continue;
            }

            try {

                String htmlPreview = trim(message.getBodyHtml(), 200);
                String textPreview = trim(message.getBodyText(), 200);
                log.debug("[Worker] id={} Enviando → recipient={}, subject='{}', body(htmlPreview)='{}', plain_body(preview)='{}'",
                        id, safeEmail(message.getTo()), trim(message.getSubject(), 200), htmlPreview, textPreview);

                var res = emailClient.sendEmail(
                	message.getFrom(),
                    message.getTo(),       
                    message.getSubject(),  
                    message.getBodyText(), 
                    message.getBodyHtml()   
                );


                int status = res.responseStatusCode;
                log.info("[Worker] id={} Respuesta HTTP {}", id, status);

                if (res.responseStatusCode >= 200 && res.responseStatusCode < 300) {
                    repo.markSent(id, Instant.now(), null);

	                String providerMessageId = res.messageId;
	                repo.setProviderMessageId(id, providerMessageId);

					
                } else {
                    repo.markFailed(id, "Error HTTP " + res.responseStatusCode);
                    log.warn("[Worker] id={} marcado como FAILED por HTTP {}", id, status);
                }
            } catch (Exception ex) {
            	log.error("[Worker] id={} Excepción enviando al proveedor: {}", id, ex.toString(), ex);
                repo.markFailed(id, ex.getMessage());
            }
        }
    }

    private static String safeEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];
        String maskedLocal = local.length() <= 2 ? "***" : local.substring(0, 2) + "***";
        return maskedLocal + "@" + domain;
    }
    

    private static String trim(String s, int max) {
        if (s == null) return null;
        String flat = s.replaceAll("\\s+", " ").trim();
        return flat.length() > max ? flat.substring(0, max) + "…" : flat;
    }

}