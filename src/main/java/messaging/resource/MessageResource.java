package messaging.resource;

import messaging.controller.MessageController;
import messaging.dto.BulkMessageRequest;
import messaging.dto.EnqueueResponse;
import messaging.dto.MessageRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class MessageResource {

    @Inject
    MessageController controller;

    @POST
    public Response enqueueOne(MessageRequest request) {
        Long id = controller.enqueue(request);
        return Response.accepted(new EnqueueResponse(java.util.List.of(id))).build();
    }

    @POST
    @Path("/bulk")
    public Response enqueueBulk(BulkMessageRequest request) {
        var ids = controller.enqueueBulk(request.messages);
        return Response.accepted(new EnqueueResponse(ids)).build();
    }
}