package messaging.dto;

import jakarta.annotation.Nonnull;
import java.util.List;

public class BulkMessageRequest {

	@Nonnull
    public List<MessageRequest> messages;
}