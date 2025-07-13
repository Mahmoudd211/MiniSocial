package com.minisocial.util;

import com.minisocial.dto.NotificationEvent;
import com.minisocial.entity.Group;
import com.minisocial.entity.GroupPost;
import com.minisocial.entity.Notification;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import com.minisocial.event.EventType;
import com.minisocial.event.NotificationEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;

/**
 * Utility class to manage notifications using the event-driven architecture.
 */
@ApplicationScoped
public class NotificationManager {
    
    private static NotificationEventService eventService;
    
    // Static singleton instance for use by static methods
    private static NotificationManager instance;
    
    @Inject
    public NotificationManager(NotificationEventService eventService) {
        NotificationManager.eventService = eventService;
        NotificationManager.instance = this;
    }
    
    /**
     * Creates a notification for a friend request
     * 
     * @param recipientId The user ID of the recipient
     * @param senderId The user ID of the sender
     */
    public static void sendFriendRequestNotification(Long recipientId, Long senderId) {
        if (eventService != null) {
            try {
                eventService.publishNotificationEvent(
                    EventType.FRIEND_REQUEST_SENT,
                    recipientId,
                    senderId,
                    getUserName(senderId) + " sent you a friend request",
                    "FRIEND_REQUEST",
                    senderId
                );
            } catch (Exception e) {
                // Fallback to direct database operation
                createFriendRequestNotificationDirect(recipientId, senderId);
            }
        } else {
            // Fallback to direct database operation
            createFriendRequestNotificationDirect(recipientId, senderId);
        }
    }
    
    /**
     * Creates a notification when a user likes a post
     * 
     * @param postId The ID of the post that was liked
     * @param likerId The ID of the user who liked the post
     */
    public static void sendPostLikedNotification(Long postId, Long likerId) {
        // Get post author ID first
        EntityManager em = JPAUtil.getEntityManager();
        Long recipientId = null;
        try {
            // Try to find a regular post first
            Post post = em.find(Post.class, postId);
            if (post != null) {
                // Don't notify if the user likes their own post
                if (post.getAuthor().getId().equals(likerId)) {
                    return;
                }
                recipientId = post.getAuthor().getId();
            } else {
                // If not found, try to find a group post
                GroupPost groupPost = em.find(GroupPost.class, postId);
                if (groupPost == null) {
                    return; // Neither post type found
                }
                
                // Don't notify if the user likes their own post
                if (groupPost.getAuthor().getId().equals(likerId)) {
                    return;
                }
                recipientId = groupPost.getAuthor().getId();
            }
        } finally {
            em.close();
        }
        
        if (recipientId == null) {
            return;
        }
        
        if (eventService != null) {
            try {
                eventService.publishNotificationEvent(
                    EventType.POST_LIKED,
                    recipientId,
                    likerId,
                    getUserName(likerId) + " liked your post",
                    "POST",
                    postId
                );
            } catch (Exception e) {
                // Fallback to direct database operation
                createPostLikedNotificationDirect(postId, likerId);
            }
        } else {
            // Fallback to direct database operation
            createPostLikedNotificationDirect(postId, likerId);
        }
    }
    
    /**
     * Creates a notification when a user comments on a post
     * 
     * @param postId The ID of the post that was commented on
     * @param commenterId The ID of the user who commented
     */
    public static void sendPostCommentedNotification(Long postId, Long commenterId) {
        // Get post author ID first
        EntityManager em = JPAUtil.getEntityManager();
        Long recipientId = null;
        try {
            // Try to find a regular post first
            Post post = em.find(Post.class, postId);
            if (post != null) {
                // Don't notify if the user comments on their own post
                if (post.getAuthor().getId().equals(commenterId)) {
                    return;
                }
                recipientId = post.getAuthor().getId();
            } else {
                // If not found, try to find a group post
                GroupPost groupPost = em.find(GroupPost.class, postId);
                if (groupPost == null) {
                    return; // Neither post type found
                }
                
                // Don't notify if the user comments on their own post
                if (groupPost.getAuthor().getId().equals(commenterId)) {
                    return;
                }
                recipientId = groupPost.getAuthor().getId();
            }
        } finally {
            em.close();
        }
        
        if (recipientId == null) {
            return;
        }
        
        if (eventService != null) {
            try {
                eventService.publishNotificationEvent(
                    EventType.POST_COMMENTED,
                    recipientId,
                    commenterId,
                    getUserName(commenterId) + " commented on your post",
                    "POST",
                    postId
                );
            } catch (Exception e) {
                // Fallback to direct database operation
                createPostCommentedNotificationDirect(postId, commenterId);
            }
        } else {
            // Fallback to direct database operation
            createPostCommentedNotificationDirect(postId, commenterId);
        }
    }
    
    /**
     * Creates a notification when a user's request to join a group is approved
     * 
     * @param userId The ID of the user who joined the group
     * @param groupId The ID of the group that was joined
     */
    public static void sendGroupJoinedNotification(Long userId, Long groupId) {
        // Get group name first
        EntityManager em = JPAUtil.getEntityManager();
        String groupName = null;
        try {
            Group group = em.find(Group.class, groupId);
            if (group == null) {
                return; // Group not found
            }
            
            groupName = group.getName();
        } finally {
            em.close();
        }
        
        if (groupName == null) {
            return;
        }
        
        if (eventService != null) {
            try {
                eventService.publishNotificationEvent(
                    EventType.GROUP_JOIN_APPROVED,
                    userId,
                    null,
                    "You have joined the group: " + groupName,
                    "GROUP",
                    groupId
                );
            } catch (Exception e) {
                // Fallback to direct database operation
                createGroupJoinedNotificationDirect(userId, groupId);
            }
        } else {
            // Fallback to direct database operation
            createGroupJoinedNotificationDirect(userId, groupId);
        }
    }
    
    /**
     * Creates a notification when a user is removed from or leaves a group
     * 
     * @param userId The ID of the user who left the group
     * @param groupId The ID of the group that was left
     */
    public static void sendGroupLeftNotification(Long userId, Long groupId) {
        // Get group name first
        EntityManager em = JPAUtil.getEntityManager();
        String groupName = null;
        try {
            Group group = em.find(Group.class, groupId);
            if (group == null) {
                return; // Group not found
            }
            
            groupName = group.getName();
        } finally {
            em.close();
        }
        
        if (groupName == null) {
            return;
        }
        
        if (eventService != null) {
            eventService.publishNotificationEvent(
                EventType.GROUP_LEFT,
                userId,
                null,
                "You are no longer a member of the group: " + groupName,
                "GROUP",
                groupId
            );
        } else {
            System.err.println("Event service is not available. Unable to send group left notification.");
        }
    }
    
    // Legacy direct database methods preserved for backward compatibility
    
    private static void createFriendRequestNotificationDirect(Long recipientId, Long senderId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User recipient = em.find(User.class, recipientId);
            User sender = em.find(User.class, senderId);
            
            if (recipient == null || sender == null) {
                return; // Users not found
            }
            
            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setSender(sender);
            notification.setEventType(NotificationEvent.EventType.FRIEND_REQUEST_RECEIVED);
            notification.setMessage("You have received a friend request from " + sender.getName());
            notification.setEntityType("FRIEND_REQUEST");
            notification.setEntityId(senderId);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating notification: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    private static void createPostLikedNotificationDirect(Long postId, Long likerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Get post author ID
            Post post = em.find(Post.class, postId);
            if (post == null) {
                GroupPost groupPost = em.find(GroupPost.class, postId);
                if (groupPost == null) {
                    return;
                }
                // Don't notify if the user likes their own post
                if (groupPost.getAuthor().getId().equals(likerId)) {
                    return;
                }
                
                Notification notification = new Notification();
                notification.setEventType(NotificationEvent.EventType.POST_LIKED);
                notification.setRecipient(groupPost.getAuthor());
                notification.setSender(em.find(User.class, likerId));
                notification.setMessage(getUserName(likerId) + " liked your post");
                notification.setEntityType("POST");
                notification.setEntityId(postId);
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                
                em.persist(notification);
            } else {
                // Don't notify if the user likes their own post
                if (post.getAuthor().getId().equals(likerId)) {
                    return;
                }
                
                Notification notification = new Notification();
                notification.setEventType(NotificationEvent.EventType.POST_LIKED);
                notification.setRecipient(post.getAuthor());
                notification.setSender(em.find(User.class, likerId));
                notification.setMessage(getUserName(likerId) + " liked your post");
                notification.setEntityType("POST");
                notification.setEntityId(postId);
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                
                em.persist(notification);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    private static void createPostCommentedNotificationDirect(Long postId, Long commenterId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Get post author ID
            Post post = em.find(Post.class, postId);
            if (post == null) {
                GroupPost groupPost = em.find(GroupPost.class, postId);
                if (groupPost == null) {
                    return;
                }
                // Don't notify if the user comments on their own post
                if (groupPost.getAuthor().getId().equals(commenterId)) {
                    return;
                }
                
                Notification notification = new Notification();
                notification.setEventType(NotificationEvent.EventType.POST_COMMENTED);
                notification.setRecipient(groupPost.getAuthor());
                notification.setSender(em.find(User.class, commenterId));
                notification.setMessage(getUserName(commenterId) + " commented on your post");
                notification.setEntityType("POST");
                notification.setEntityId(postId);
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                
                em.persist(notification);
            } else {
                // Don't notify if the user comments on their own post
                if (post.getAuthor().getId().equals(commenterId)) {
                    return;
                }
                
                Notification notification = new Notification();
                notification.setEventType(NotificationEvent.EventType.POST_COMMENTED);
                notification.setRecipient(post.getAuthor());
                notification.setSender(em.find(User.class, commenterId));
                notification.setMessage(getUserName(commenterId) + " commented on your post");
                notification.setEntityType("POST");
                notification.setEntityId(postId);
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                
                em.persist(notification);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    private static void createGroupJoinedNotificationDirect(Long userId, Long groupId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            Group group = em.find(Group.class, groupId);
            
            if (user == null || group == null) {
                return; // User or group not found
            }
            
            Notification notification = new Notification();
            notification.setRecipient(user);
            notification.setEventType(NotificationEvent.EventType.GROUP_JOINED);
            notification.setMessage("You have joined the group: " + group.getName());
            notification.setEntityType("GROUP");
            notification.setEntityId(groupId);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating notification: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    private static void createGroupLeftNotificationDirect(Long userId, Long groupId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            Group group = em.find(Group.class, groupId);
            
            if (user == null || group == null) {
                return; // User or group not found
            }
            
            Notification notification = new Notification();
            notification.setRecipient(user);
            notification.setEventType(NotificationEvent.EventType.GROUP_LEFT);
            notification.setMessage("You are no longer a member of the group: " + group.getName());
            notification.setEntityType("GROUP");
            notification.setEntityId(groupId);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating notification: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    // Helper method to get user name
    private static String getUserName(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            if (user != null) {
                return user.getName();
            }
            return "Unknown User";
        } finally {
            em.close();
        }
    }
} 