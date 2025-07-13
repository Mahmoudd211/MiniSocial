package com.minisocial.rest;

import com.minisocial.dto.GroupDTO;
import com.minisocial.dto.GroupMembershipRequestDTO;
import com.minisocial.service.GroupService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {
    
    @Inject
    private GroupService groupService;

    @POST
    public Response createGroup(
            @QueryParam("name") String name,
            @QueryParam("description") String description,
            @QueryParam("isOpen") boolean isOpen,
            @QueryParam("creatorId") Long creatorId) {
        try {
            GroupDTO group = groupService.createGroup(name, description, isOpen, creatorId);
            return Response.status(Response.Status.CREATED).entity(group).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId) {
        try {
            GroupDTO group = groupService.getGroup(groupId);
            return Response.ok(group).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{groupId}/join")
    public Response requestJoinGroup(
            @PathParam("groupId") Long groupId,
            @QueryParam("userId") Long userId) {
        try {
            GroupMembershipRequestDTO request = groupService.requestJoinGroup(groupId, userId);
            return Response.status(Response.Status.CREATED).entity(request).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/requests/{requestId}/approve")
    public Response approveJoinRequest(
            @PathParam("requestId") Long requestId,
            @QueryParam("adminId") Long adminId) {
        try {
            GroupDTO group = groupService.approveJoinRequest(requestId, adminId);
            return Response.ok(group).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/requests/{requestId}/reject")
    public Response rejectJoinRequest(
            @PathParam("requestId") Long requestId,
            @QueryParam("adminId") Long adminId) {
        try {
            GroupDTO group = groupService.rejectJoinRequest(requestId, adminId);
            return Response.ok(group).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{groupId}/promote")
    public Response promoteToAdmin(
            @PathParam("groupId") Long groupId,
            @QueryParam("userId") Long userId,
            @QueryParam("adminId") Long adminId) {
        try {
            GroupDTO group = groupService.promoteToAdmin(groupId, userId, adminId);
            return Response.ok(group).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{groupId}/members/{userId}")
    public Response removeMember(
            @PathParam("groupId") Long groupId,
            @PathParam("userId") Long userId,
            @QueryParam("adminId") Long adminId) {
        try {
            groupService.removeMember(groupId, userId, adminId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{groupId}/leave")
    public Response leaveGroup(
            @PathParam("groupId") Long groupId,
            @QueryParam("userId") Long userId) {
        try {
            groupService.leaveGroup(groupId, userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{groupId}")
    public Response deleteGroup(
            @PathParam("groupId") Long groupId,
            @QueryParam("adminId") Long adminId) {
        try {
            groupService.deleteGroup(groupId, adminId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/member/{userId}")
    public Response getGroupsByMember(@PathParam("userId") Long userId) {
        try {
            List<GroupDTO> groups = groupService.getGroupsByMember(userId);
            return Response.ok(groups).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/admin/{userId}")
    public Response getGroupsByAdmin(@PathParam("userId") Long userId) {
        try {
            List<GroupDTO> groups = groupService.getGroupsByAdmin(userId);
            return Response.ok(groups).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 