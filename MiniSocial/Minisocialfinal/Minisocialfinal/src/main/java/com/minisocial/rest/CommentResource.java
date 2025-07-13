package com.minisocial.rest;

import com.minisocial.dto.CommentDTO;
import com.minisocial.service.CommentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentResource {
    @Inject
    private CommentService commentService;

    @POST
    @Path("/{postId}")
    public Response createComment(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId,
            CommentDTO commentDTO) {
        try {
            CommentDTO createdComment = commentService.createComment(postId, userId, commentDTO);
            return Response.status(Response.Status.CREATED).entity(createdComment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{commentId}")
    public Response updateComment(
            @PathParam("commentId") Long commentId,
            @QueryParam("userId") Long userId,
            CommentDTO commentDTO) {
        try {
            CommentDTO updatedComment = commentService.updateComment(commentId, userId, commentDTO);
            return Response.ok(updatedComment).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{commentId}")
    public Response deleteComment(
            @PathParam("commentId") Long commentId,
            @QueryParam("userId") Long userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/post/{postId}")
    public Response getPostComments(@PathParam("postId") Long postId) {
        try {
            List<CommentDTO> comments = commentService.getPostComments(postId);
            return Response.ok(comments).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 