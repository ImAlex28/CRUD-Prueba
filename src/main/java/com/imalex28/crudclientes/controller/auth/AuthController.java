
package com.imalex28.crudclientes.controller.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

import com.imalex28.crudclientes.dto.auth.CredentialsRequestDTO;
import com.imalex28.crudclientes.dto.auth.LoginResponseDTO;
import com.imalex28.crudclientes.service.auth.AuthService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    JwtGenerator jwtGenerator;
    
    @Inject
    AuthService authService;


    /**
     * ENDPOINT to generate a JWT Token for user.
     * POST /auth/login
     * Body:
     * {
     *   "username": "alex28",
     *   "password": "1234"
     * }
     */
    @POST
    @Path("/login")
    public Response userLogin(CredentialsRequestDTO request) {
        try {

            // Delegate verification to the service (H2 repo, hashing, etc.)
            String jwt = authService.loginAndIssueToken(request.getUsername(), request.getPassword());
            return Response.ok(new LoginResponseDTO(jwt)).build();

        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    /**
     * ENDPOINT to generate a JWT Token with admin role. At this time, neither role or password validation is performed.
     * Body is not required, it'll generate a default admin token. ENDPOINT just for testing, not final.
     */
    @POST
    @Path("/admin/login")
    public Response adminLogin() {
        try {
            // Admin with role "admin" generated directly invoking jwtGenerator
            String token = jwtGenerator.generateToken("alex28", new String[] { "admin" }, 3600);
            return Response.ok(Map.of("token", token)).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }
}
