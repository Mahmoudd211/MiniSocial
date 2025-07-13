package com.minisocial.rest;

import com.minisocial.dto.UserDTO;
import com.minisocial.entity.User;
import com.minisocial.service.UserSearchService;
import com.minisocial.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserSearchResource {
    
    private final UserSearchService userSearchService = new UserSearchService();
    
    @GET
    @Path("/query")
    public Response searchUsers(@QueryParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Search query cannot be empty")
                .build();
        }
        
        List<User> users = userSearchService.searchUsers(query);
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
            
        return Response.ok(userDTOs).build();
    }
    
    @GET
    @Path("/suggestions")
    public Response getFriendSuggestions(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        // Get user ID from email (you might want to add a method in UserService for this)
        User user = new UserService().getUserByEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("User not found")
                .build();
        }
        
        List<User> suggestions = userSearchService.getFriendSuggestions(user.getId());
        List<UserDTO> suggestionDTOs = suggestions.stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
            
        return Response.ok(suggestionDTOs).build();
    }
} 