package com.minisocial.event;

/**
 * Enum representing all possible event types in the system.
 */
public enum EventType {
    // User-related events
    USER_REGISTERED,
    USER_UPDATED,
    
    // Friendship events
    FRIEND_REQUEST_SENT,
    FRIEND_REQUEST_ACCEPTED,
    FRIEND_REQUEST_REJECTED,
    
    // Post-related events
    POST_CREATED,
    POST_UPDATED,
    POST_DELETED,
    POST_LIKED,
    POST_COMMENTED,
    
    // Group-related events
    GROUP_CREATED,
    GROUP_JOIN_REQUESTED,
    GROUP_JOIN_APPROVED,
    GROUP_JOIN_REJECTED,
    GROUP_LEFT,
    GROUP_MEMBER_REMOVED,
    
    // Notification events
    NOTIFICATION_CREATED,
    NOTIFICATION_READ
} 