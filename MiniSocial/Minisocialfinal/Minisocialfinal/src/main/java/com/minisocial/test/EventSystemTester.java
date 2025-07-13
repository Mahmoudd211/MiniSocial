package com.minisocial.test;

import com.minisocial.event.EventType;
import com.minisocial.event.NotificationEventService;
import com.minisocial.event.SocialEvent;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.util.logging.Logger;

/**
 * Utility class to test the event system.
 * This is for testing and verification purposes only.
 */
@Singleton
@Startup
public class EventSystemTester {
    
    private static final Logger LOGGER = Logger.getLogger(EventSystemTester.class.getName());
    
    @Inject
    private NotificationEventService eventService;
    
    /**
     * Sends a test event to verify the event system is working.
     */
    public void sendTestEvent() {
        if (eventService != null) {
            LOGGER.info("Sending test event to verify the event system...");
            
            SocialEvent testEvent = new SocialEvent(EventType.USER_REGISTERED.name())
                    .addData("userId", 1L)
                    .addData("username", "testuser")
                    .addData("email", "test@example.com")
                    .addData("timestamp", System.currentTimeMillis())
                    .addData("message", "This is a test event");
            
            eventService.publishEvent(testEvent);
            
            LOGGER.info("Test event sent successfully!");
        } else {
            LOGGER.severe("Event service is not available. Unable to send test event.");
        }
    }
    
    /**
     * Creates and sends various test events to verify all components are working correctly.
     */
    public void runFullTest() {
        if (eventService == null) {
            LOGGER.severe("Event service is not available. Unable to run tests.");
            return;
        }
        
        LOGGER.info("Running comprehensive event system test...");
        
        // Test 1: Friend request event
        SocialEvent friendRequestEvent = new SocialEvent(EventType.FRIEND_REQUEST_SENT.name())
                .addData("recipientId", 2L)
                .addData("senderId", 1L)
                .addData("message", "Test User sent you a friend request")
                .addData("entityType", "FRIEND_REQUEST")
                .addData("entityId", 1L);
        
        eventService.publishEvent(friendRequestEvent);
        LOGGER.info("Test 1: Friend request event sent");
        
        // Test 2: Post liked event
        SocialEvent postLikedEvent = new SocialEvent(EventType.POST_LIKED.name())
                .addData("recipientId", 2L)
                .addData("senderId", 1L)
                .addData("message", "Test User liked your post")
                .addData("entityType", "POST")
                .addData("entityId", 10L);
        
        eventService.publishEvent(postLikedEvent);
        LOGGER.info("Test 2: Post liked event sent");
        
        // Test 3: Group joined event
        SocialEvent groupJoinedEvent = new SocialEvent(EventType.GROUP_JOIN_APPROVED.name())
                .addData("recipientId", 3L)
                .addData("senderId", null)
                .addData("message", "You have joined the group: Test Group")
                .addData("entityType", "GROUP")
                .addData("entityId", 5L);
        
        eventService.publishEvent(groupJoinedEvent);
        LOGGER.info("Test 3: Group joined event sent");
        
        LOGGER.info("All test events sent successfully!");
    }
} 