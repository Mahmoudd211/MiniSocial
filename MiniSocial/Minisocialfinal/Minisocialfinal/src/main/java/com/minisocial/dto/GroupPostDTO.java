package com.minisocial.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class GroupPostDTO {
    private Long id;
    private GroupDTO group;
    private UserDTO author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<GroupPostCommentDTO> comments;
    private Set<GroupPostLikeDTO> likes;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupDTO getGroup() {
        return group;
    }

    public void setGroup(GroupDTO group) {
        this.group = group;
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

    public Set<GroupPostCommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<GroupPostCommentDTO> comments) {
        this.comments = comments;
    }

    public Set<GroupPostLikeDTO> getLikes() {
        return likes;
    }

    public void setLikes(Set<GroupPostLikeDTO> likes) {
        this.likes = likes;
    }
} 