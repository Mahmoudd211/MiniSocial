package com.minisocial.dto;

import com.minisocial.entity.Comment;
import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private Long postId;
    private UserDTO author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public CommentDTO() {
    }

    // Constructor from Comment entity
    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.author = new UserDTO(comment.getAuthor());
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
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

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 