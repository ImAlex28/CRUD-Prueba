package messaging.dto;

public record SendEmailRequest(
    String recipient,
    String subject,
    String body,
    String plain_body
) {}

