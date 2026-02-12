package messaging.apiclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import messaging.dto.SendEmailRequest;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

@Path("/v1/email")
@RegisterRestClient(configKey = "notified")
@RegisterProvider(NotifiedAuthFilter.class)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public interface NotifiedEmailClient {

    @POST
    @Path("/send/")
    RestResponse<String> sendEmail(
        @FormParam("recipient")  String recipient,
        @FormParam("subject")    String subject,
        @FormParam("body")       String body,
        @FormParam("plain_body") String plainBody
    );
}

