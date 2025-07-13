package com.minisocial.service;

import com.minisocial.dto.NotificationEvent;
import com.minisocial.event.EventType;
import com.minisocial.event.NotificationEventService;
import com.minisocial.event.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service for managing notifications.
 * This is now a wrapper around the event-driven notification system.
 */
@ApplicationScoped
public class NotificationService {
    
    @Inject
    private NotificationEventService eventService;
    
    /**
     * Sends a notification event
     */
    public void sendNotification(NotificationEvent.EventType notifEventType, Long recipientId, Long senderId, 
                               String message, String entityType, Long entityId) {
        
        // Convert from old notification event type to new event type
        EventType eventType;
        switch (notifEventType) {
            case FRIEND_REQUEST_RECEIVED:
                eventType = EventType.FRIEND_REQUEST_SENT;
                break;
            case POST_LIKED:
                eventType = EventType.POST_LIKED;
                break;
            case POST_COMMENTED:
                eventType = EventType.POST_COMMENTED;
                break;
            case GROUP_JOINED:
                eventType = EventType.GROUP_JOIN_APPROVED;
                break;
            case GROUP_LEFT:
                eventType = EventType.GROUP_LEFT;
                break;
            default:
                eventType = EventType.NOTIFICATION_CREATED;
        }
        
        // Create and publish the event
        SocialEvent event = new SocialEvent(eventType.name())
            .addData("recipientId", recipientId)
            .addData("senderId", senderId)
            .addData("message", message)
            .addData("entityType", entityType)
            .addData("entityId", entityId);
        
        eventService.publishEvent(event);
    }

    // Convenience methods for different notification types
    public void sendFriendRequestNotification(Long recipientId, Long senderId) {
        sendNotification(
            NotificationEvent.EventType.FRIEND_REQUEST_RECEIVED,
            recipientId,
            senderId,
            "You have received a new friend request",
            "FRIEND_REQUEST",
            senderId
        );
    }

    public void sendPostLikedNotification(Long recipientId, Long senderId, Long postId) {
        sendNotification(
            NotificationEvent.EventType.POST_LIKED,
            recipientId,
            senderId,
            "Someone liked your post",
            "POST",
            postId
        );
    }

    public void sendPostCommentedNotification(Long recipientId, Long senderId, Long postId) {
        sendNotification(
            NotificationEvent.EventType.POST_COMMENTED,
            recipientId,
            senderId,
            "Someone commented on your post",
            "POST",
            postId
        );
    }

    public void sendGroupJoinedNotification(Long recipientId, Long groupId) {
        sendNotification(
            NotificationEvent.EventType.GROUP_JOINED,
            recipientId,
            null,
            "You have joined a new group",
            "GROUP",
            groupId
        );
    }

    public void sendGroupLeftNotification(Long recipientId, Long groupId) {
        sendNotification(
            NotificationEvent.EventType.GROUP_LEFT,
            recipientId,
            null,
            "You have left a group",
            "GROUP",
            groupId
        );
    }
} 