package nameshortener;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/names")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NameShortenerResource {

    @Inject
    NameShortener nameShortener;

    /**
     * POST con JSON:
     * {
     *   "givenNames": "Marco Antonio",
     *   "surname1": "Torres",
     *   "surname2": "Molina"
     * }
     */
    @POST
    @Path("/shorten")
    public Response shortenPost(ShortenRequest request) {
        if (request == null) {
            throw new BadRequestException("Body requerido.");
        }
        var variants = nameShortener.cardNameShortener(request.givenNames, request.surname1, request.surname2);
        return Response.ok(
        		List.of(
        			    new ShortenResponse(1, variants.get(0)),
        			    new ShortenResponse(2, variants.get(1)),
        			    new ShortenResponse(3, variants.get(2))
        			)).build();
    }
}
