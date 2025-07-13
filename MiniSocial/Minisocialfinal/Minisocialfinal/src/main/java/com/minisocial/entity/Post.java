package com.minisocial.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@NamedQueries({
    @NamedQuery(name = "Post.findByUser", 
                query = "SELECT p FROM Post p " +
                       "LEFT JOIN FETCH p.author " +
                       "WHERE p.author.id = :userId " +
                       "ORDER BY p.createdAt DESC"),
    @NamedQuery(name = "Post.findByFriends", 
                query = "SELECT p FROM Post p " +
                       "LEFT JOIN FETCH p.author " +
                       "WHERE p.author.id IN " +
                       "(SELECT CASE " +
                       "   WHEN fr.sender.id = :userId THEN fr.receiver.id " +
                       "   ELSE fr.sender.id " +
                       "END " +
                       "FROM FriendRequest fr " +
                       "WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) " +
                       "AND fr.status = 'ACCEPTED') " +
                       "ORDER BY p.createdAt DESC"),
    @NamedQuery(name = "Post.findById",
                query = "SELECT p FROM Post p " +
                       "LEFT JOIN FETCH p.author " +
                       "WHERE p.id = :postId")
})
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 1000)
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private Set<String> imageUrls = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_links", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "link_url")
    private Set<String> linkUrls = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Default constructor
    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
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

    public void addImageUrl(String imageUrl) {
        this.imageUrls.add(imageUrl);
    }

    public void removeImageUrl(String imageUrl) {
        this.imageUrls.remove(imageUrl);
    }

    public Set<String> getLinkUrls() {
        return linkUrls;
    }

    public void setLinkUrls(Set<String> linkUrls) {
        this.linkUrls = linkUrls;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
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

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 