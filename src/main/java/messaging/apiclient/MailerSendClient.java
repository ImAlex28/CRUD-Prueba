package messaging.apiclient;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MailerSendClient {

    private static final Logger log = LoggerFactory.getLogger(MailerSendClient.class);

    @ConfigProperty(name = "mailersend.api.token")
    String apiToken;

    public MailerSendResponse sendEmail(String from, String to, String subject, String text, String html) throws MailerSendException {

        if (apiToken == null || apiToken.isBlank()) {
            throw new MailerSendException("MailerSend API token no configurado (mailersend.api.token).");
        }
        if (from == null || to == null) {
            throw new MailerSendException("Campos 'from' y 'to' son obligatorios.");
        }

        Email email = new Email();

        email.setFrom(from, from);
        email.addRecipient(to, to);

        email.setSubject(subject);
        if (text != null && !text.isBlank()) {
            email.setPlain(text);
        }
        if (html != null && !html.isBlank()) {
            email.setHtml(html);
        }

        MailerSend ms = new MailerSend();
        ms.setToken(apiToken); 

        MailerSendResponse response = ms.emails().send(email);
        log.debug("MailerSend: status={}, messageId={}", response.responseStatusCode, response.messageId);
        return response;
    }
}