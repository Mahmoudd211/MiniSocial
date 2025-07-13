package com.minisocial.dto;

import com.minisocial.entity.Post;
import java.time.LocalDateTime;
import java.util.Set;

public class PostDTO {
    private Long id;
    private UserDTO author;
    private String content;
    private Set<String> imageUrls;
    private Set<String> linkUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public PostDTO() {
    }

    // Constructor from Post entity
    public PostDTO(Post post) {
        this.id = post.getId();
        this.author = new UserDTO(post.getAuthor());
        this.content = post.getContent();
        this.imageUrls = post.getImageUrls();
        this.linkUrls = post.getLinkUrls();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(Set<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Set<String> getLinkUrls() {
        return linkUrls;
    }

    public void setLinkUrls(Set<String> linkUrls) {
        this.linkUrls = linkUrls;
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