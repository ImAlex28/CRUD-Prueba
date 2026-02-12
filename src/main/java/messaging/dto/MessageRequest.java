package messaging.dto;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

public class MessageRequest {

    @Nonnull
    public String from;

    @Nonnull
    public String to;

    public List<String> cc;
    public List<String> bcc;

    @Nonnull
    public String subject;

    public String bodyText;

    public String bodyHtml;

    public Map<String, String> headers;

    public Integer priority;

}