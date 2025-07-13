package com.minisocial.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests")
@NamedQueries({
    @NamedQuery(name = "FriendRequest.findPendingByReceiver", 
                query = "SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :receiverId AND fr.status = 'PENDING'"),
    @NamedQuery(name = "FriendRequest.findBySenderAndReceiver", 
                query = "SELECT fr FROM FriendRequest fr WHERE fr.sender.id = :senderId AND fr.receiver.id = :receiverId"),
    @NamedQuery(name = "FriendRequest.findFriendsByUser", 
                query = "SELECT fr FROM FriendRequest fr WHERE (fr.sender.id = :userId OR fr.receiver.id = :userId) AND fr.status = 'ACCEPTED'")
})
public class FriendRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    // Default constructor
    public FriendRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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