package messaging.dto;

import java.util.List;

public class EnqueueResponse {
    public List<Long> ids;

    public EnqueueResponse() {}
    public EnqueueResponse(List<Long> ids) { this.ids = ids; }
}