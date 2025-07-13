package com.minisocial.rest;

import com.minisocial.dto.PostDTO;
import com.minisocial.service.PostService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostResource {

    private final PostService postService;

    public PostResource() {
        this.postService = new PostService();
    }

    @POST
    public Response createPost(@QueryParam("userId") Long userId, PostDTO postDTO) {
        try {
            PostDTO createdPost = postService.createPost(userId, postDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(createdPost)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{postId}")
    public Response updatePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId,
            PostDTO postDTO) {
        try {
            PostDTO updatedPost = postService.updatePost(postId, userId, postDTO);
            return Response.ok(updatedPost).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{postId}")
    public Response deletePost(
            @PathParam("postId") Long postId,
            @QueryParam("userId") Long userId) {
        try {
            postService.deletePost(postId, userId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserPosts(@PathParam("userId") Long userId) {
        try {
            List<PostDTO> posts = postService.getUserPosts(userId);
            return Response.ok(posts).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/friends/{userId}")
    public Response getFriendsPosts(@PathParam("userId") Long userId) {
        try {
            List<PostDTO> posts = postService.getFriendsPosts(userId);
            return Response.ok(posts).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{postId}")
    public Response getPostById(@PathParam("postId") Long postId) {
        try {
            PostDTO post = postService.getPostById(postId);
            return Response.ok(post).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
} 