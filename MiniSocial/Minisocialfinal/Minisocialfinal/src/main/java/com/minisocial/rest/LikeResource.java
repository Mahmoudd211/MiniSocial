package com.minisocial.rest;

import com.minisocial.dto.LikeDTO;
import com.minisocial.service.LikeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/likes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LikeResource {
    @Inject
    private LikeService likeService;

    @POST
    @Path("/post/{postId}")
    public Response likePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId) {
        try {
            LikeDTO like = likeService.likePost(postId, userId);
            return Response.status(Response.Status.CREATED).entity(like).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/post/{postId}")
    public Response unlikePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId) {
        try {
            likeService.unlikePost(postId, userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/post/{postId}")
    public Response getPostLikes(@PathParam("postId") Long postId) {
        try {
            List<LikeDTO> likes = likeService.getPostLikes(postId);
            return Response.ok(likes).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/post/{postId}/user/{userId}")
    public Response hasUserLikedPost(
            @PathParam("postId") Long postId,
            @PathParam("userId") Long userId) {
        try {
            boolean hasLiked = likeService.hasUserLikedPost(postId, userId);
            return Response.ok(hasLiked).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
} 