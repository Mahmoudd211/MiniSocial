package com.minisocial.event;

import com.minisocial.dto.NotificationEvent;
import com.minisocial.entity.Notification;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean that consumes notification events from the queue
 * and processes them accordingly.
 * Now handles JSON-formatted events.
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/NotificationQueue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class NotificationEventConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationEventConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String contentType = textMessage.getStringProperty("contentType");
                String jsonContent = textMessage.getText();
                
                LOGGER.info("Received message: " + jsonContent);
                
                // Verify this is a JSON message
                if (contentType != null && contentType.equals("application/json")) {
                    // Deserialize JSON to SocialEvent
                    SocialEvent event = SocialEvent.fromJson(jsonContent);
                    LOGGER.info("Parsed event: " + event);
                    
                    // Process the event
                    processEvent(event);
                } else {
                    LOGGER.warning("Received non-JSON message: " + jsonContent);
                }
            } else {
                LOGGER.warning("Received non-text message: " + message);
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing notification message", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error processing message", e);
        }
    }
    
    private void processEvent(SocialEvent event) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LOGGER.info("Processing event: " + event.getEventType());
            
            Map<String, Object> data = event.getData();
            
            // Extract data from the event
            Long recipientId = getLongValue(data, "recipientId");
            Long senderId = getLongValue(data, "senderId");
            String message = (String) data.get("message");
            String entityType = (String) data.get("entityType");
            Long entityId = getLongValue(data, "entityId");
            String eventTypeStr = event.getEventType();
            
            // Print detailed event information for verification
            LOGGER.info("Event details:");
            LOGGER.info("  Type: " + eventTypeStr);
            LOGGER.info("  Recipient: " + recipientId);
            LOGGER.info("  Sender: " + senderId);
            LOGGER.info("  Message: " + message);
            LOGGER.info("  Entity Type: " + entityType);
            LOGGER.info("  Entity ID: " + entityId);
            
            // Convert string event type to NotificationEvent.EventType
            NotificationEvent.EventType notificationEventType;
            try {
                notificationEventType = NotificationEvent.EventType.valueOf(eventTypeStr);
            } catch (IllegalArgumentException e) {
                // Default to a generic type if the conversion fails
                LOGGER.warning("Unknown event type: " + eventTypeStr + ", using default");
                notificationEventType = NotificationEvent.EventType.FRIEND_REQUEST_RECEIVED;
            }
            
            // Get the recipient user
            User recipient = em.find(User.class, recipientId);
            if (recipient == null) {
                LOGGER.warning("Recipient user not found: " + recipientId);
                return;
            }
            
            // Get the sender user (if exists)
            User sender = null;
            if (senderId != null) {
                sender = em.find(User.class, senderId);
                if (sender == null) {
                    LOGGER.warning("Sender user not found: " + senderId);
                }
            }
            
            // Create and persist the notification
            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setSender(sender);
            notification.setMessage(message);
            notification.setEventType(notificationEventType);
            notification.setEntityType(entityType);
            notification.setEntityId(entityId);
            notification.setRead(false);
            
            em.getTransaction().begin();
            em.persist(notification);
            em.getTransaction().commit();
            
            LOGGER.info("Created notification: " + notification.getId() + " for recipient: " + recipientId);
            
            // TODO: In a real system, we might want to send a WebSocket message here to notify
            // connected clients about the new notification
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error processing notification event", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Safely extracts a Long value from the data map.
     */
    private Long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
} 