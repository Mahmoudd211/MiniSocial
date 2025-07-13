package com.minisocial.dto;

import com.minisocial.entity.Notification;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private NotificationEvent.EventType eventType;
    private Long recipientId;
    private Long senderId;
    private String senderName;
    private String message;
    private String entityType;
    private Long entityId;
    private LocalDateTime createdAt;
    private boolean read;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.eventType = notification.getEventType();
        this.recipientId = notification.getRecipient() != null ? notification.getRecipient().getId() : null;
        this.senderId = notification.getSender() != null ? notification.getSender().getId() : null;
        this.senderName = notification.getSender() != null ? notification.getSender().getName() : null;
        this.message = notification.getMessage();
        this.entityType = notification.getEntityType();
        this.entityId = notification.getEntityId();
        this.createdAt = notification.getCreatedAt();
        this.read = notification.isRead();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationEvent.EventType getEventType() {
        return eventType;
    }

    public void setEventType(NotificationEvent.EventType eventType) {
        this.eventType = eventType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
} 