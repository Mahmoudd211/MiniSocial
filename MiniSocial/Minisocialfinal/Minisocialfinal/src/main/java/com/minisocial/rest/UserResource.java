package com.minisocial.rest;

import com.minisocial.dto.UserDTO;
import com.minisocial.entity.User;
import com.minisocial.security.JwtTokenProvider;
import com.minisocial.security.RequiresRole;
import com.minisocial.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @Inject
    private UserService userService;
    
    @Inject
    private JwtTokenProvider tokenProvider;
    
    @POST
    @Path("/register")
    public Response registerUser(UserDTO userDTO) {
        try {
            UserDTO user = userService.registerUser(userDTO);
            String token = tokenProvider.createToken(user.getEmail(), user.getRole().name());
            return Response.status(Response.Status.CREATED)
                    .entity(new UserDTO(user))
                    .header("Authorization", "Bearer " + token)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @POST
    @Path("/login")
    public Response login(@QueryParam("email") String email, @QueryParam("password") String password) {
        try {
            User user = userService.authenticateUser(email, password);
            String token = tokenProvider.createToken(user.getEmail(), user.getRole().name());
            return Response.ok(new UserDTO(user))
                    .header("Authorization", "Bearer " + token)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid credentials")
                    .build();
        }
    }
    
    @GET
    @RequiresRole("ADMIN")
    public Response getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return Response.ok(users).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        UserDTO user = userService.getUserById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Only allow users to view their own profile or admins to view any profile
        if (!securityContext.isUserInRole("ADMIN") && 
            !securityContext.getUserPrincipal().getName().equals(user.getEmail())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        return Response.ok(new UserDTO(user)).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, UserDTO userDTO, 
                             @Context SecurityContext securityContext) {
        UserDTO existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Only allow users to update their own profile or admins to update any profile
        if (!securityContext.isUserInRole("ADMIN") && 
            !securityContext.getUserPrincipal().getName().equals(existingUser.getEmail())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return Response.ok(new UserDTO(updatedUser)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @RequiresRole("ADMIN")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
} 