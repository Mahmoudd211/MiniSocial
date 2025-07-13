package com.minisocial.dto;

import com.minisocial.entity.GroupMembershipRequest;


import java.time.LocalDateTime;

public class GroupMembershipRequestDTO {
    private Long id;
    private GroupDTO group;
    private UserDTO user;
    private GroupMembershipRequest.RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public GroupMembershipRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(GroupMembershipRequest.RequestStatus status) {
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