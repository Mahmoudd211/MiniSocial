package com.minisocial.dto;
import java.time.LocalDateTime;
import java.util.Set;


public class GroupDTO {
    private Long id;
    private String name;
    private String description;
    private UserDTO creator;
    private Set<UserDTO> members;
    private Set<UserDTO> admins;
    private boolean isOpen;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public Set<UserDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDTO> members) {
        this.members = members;
    }

    public Set<UserDTO> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<UserDTO> admins) {
        this.admins = admins;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
