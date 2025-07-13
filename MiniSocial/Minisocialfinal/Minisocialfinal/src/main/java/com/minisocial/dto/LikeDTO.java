package com.minisocial.dto;

import com.minisocial.entity.Like;
import java.time.LocalDateTime;

public class LikeDTO {
    private Long id;
    private Long postId;
    private UserDTO user;
    private LocalDateTime createdAt;

    // Default constructor
    public LikeDTO() {
    }

    // Constructor from Like entity
    public LikeDTO(Like like) {
        this.id = like.getId();
        this.postId = like.getPost().getId();
        this.user = new UserDTO(like.getUser());
        this.createdAt = like.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 