package com.minisocial.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_post_likes")
@NamedQueries({
    @NamedQuery(name = "GroupPostLike.findByPost", 
                query = "SELECT l FROM GroupPostLike l WHERE l.post = :post"),
    @NamedQuery(name = "GroupPostLike.findByUser", 
                query = "SELECT l FROM GroupPostLike l WHERE l.user = :user"),
    @NamedQuery(name = "GroupPostLike.findByUserAndPost", 
                query = "SELECT l FROM GroupPostLike l WHERE l.user = :user AND l.post = :post")
})
public class GroupPostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private GroupPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupPost getPost() {
        return post;
    }

    public void setPost(GroupPost post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 