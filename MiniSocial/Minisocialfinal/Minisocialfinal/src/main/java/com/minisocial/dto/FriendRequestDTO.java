package com.minisocial.dto;

import com.minisocial.entity.FriendRequest;
import java.time.LocalDateTime;

public class FriendRequestDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private FriendRequest.RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public FriendRequestDTO() {
    }


    public FriendRequestDTO(FriendRequest friendRequest) {
        this.id = friendRequest.getId();
        this.sender = new UserDTO(friendRequest.getSender());
        this.receiver = new UserDTO(friendRequest.getReceiver());
        this.status = friendRequest.getStatus();
        this.createdAt = friendRequest.getCreatedAt();
        this.updatedAt = friendRequest.getUpdatedAt();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }

    public FriendRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequest.RequestStatus status) {
        this.status = status;
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