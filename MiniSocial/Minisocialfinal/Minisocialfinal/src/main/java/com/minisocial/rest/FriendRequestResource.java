package com.minisocial.rest;

import com.minisocial.dto.FriendRequestDTO;
import com.minisocial.dto.UserDTO;
import com.minisocial.service.FriendRequestService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendRequestResource {

    private final FriendRequestService friendRequestService;

    public FriendRequestResource() {
        this.friendRequestService = new FriendRequestService();
    }

    @POST
    @Path("/request/{receiverId}")
    public Response sendFriendRequest(@PathParam("receiverId") Long receiverId, @QueryParam("senderId") Long senderId) {
        try {
            FriendRequestDTO friendRequest = friendRequestService.sendFriendRequest(senderId, receiverId);
            return Response.status(Response.Status.CREATED)
                    .entity(friendRequest)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/request/{requestId}/respond")
    public Response respondToFriendRequest(
            @PathParam("requestId") Long requestId,
            @QueryParam("userId") Long userId,
            @QueryParam("accept") boolean accept) {
        try {
            FriendRequestDTO friendRequest = friendRequestService.respondToFriendRequest(requestId, userId, accept);
            return Response.ok(friendRequest).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/pending")
    public Response getPendingFriendRequests(@QueryParam("userId") Long userId) {
        try {
            List<FriendRequestDTO> pendingRequests = friendRequestService.getPendingFriendRequests(userId);
            return Response.ok(pendingRequests).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/list")
    public Response getFriends(@QueryParam("userId") Long userId) {
        try {
            List<UserDTO> friends = friendRequestService.getFriends(userId);
            return Response.ok(friends).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
} 