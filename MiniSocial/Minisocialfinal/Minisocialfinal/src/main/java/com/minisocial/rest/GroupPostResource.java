package com.minisocial.rest;

import com.minisocial.dto.GroupPostDTO;
import com.minisocial.dto.GroupPostCommentDTO;
import com.minisocial.dto.GroupPostLikeDTO;
import com.minisocial.service.GroupPostService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/group-posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupPostResource {
    
    @Inject
    private GroupPostService groupPostService;

    @POST
    public Response createPost(
            @QueryParam("groupId") Long groupId,
            @QueryParam("userId") Long userId,
            @QueryParam("content") String content) {
        try {
            GroupPostDTO post = groupPostService.createPost(groupId, userId, content);
            return Response.status(Response.Status.CREATED).entity(post).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupPosts(@PathParam("groupId") Long groupId) {
        try {
            List<GroupPostDTO> posts = groupPostService.getGroupPosts(groupId);
            return Response.ok(posts).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{postId}/comments")
    public Response addComment(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId,
            @QueryParam("content") String content) {
        try {
            GroupPostCommentDTO comment = groupPostService.addComment(postId, userId, content);
            return Response.status(Response.Status.CREATED).entity(comment).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{postId}/like")
    public Response likePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId) {
        try {
            GroupPostLikeDTO like = groupPostService.likePost(postId, userId);
            return Response.status(Response.Status.CREATED).entity(like).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{postId}")
    public Response removePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId) {
        try {
            groupPostService.removePost(postId, userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 