package com.minisocial.event;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service responsible for publishing notification events to the JMS queue.
 * Events are published as JSON strings.
 */
@Singleton
@Startup
public class NotificationEventService {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationEventService.class.getName());
    
    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;
    
    /**
     * Publishes a notification event to the JMS queue as JSON.
     * 
     * @param event The event to publish
     */
    public void publishEvent(SocialEvent event) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(notificationQueue);
            
            // Convert the event to JSON and send as a text message
            String jsonEvent = event.toJson();
            TextMessage message = session.createTextMessage(jsonEvent);
            
            // Add a property to indicate this is a JSON message
            message.setStringProperty("contentType", "application/json");
            
            producer.send(message);
            
            LOGGER.info("Published event as JSON: " + jsonEvent);
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error publishing notification event", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOGGER.log(Level.WARNING, "Error closing JMS connection", e);
                }
            }
        }
    }
    
    /**
     * Helper method to create and publish a notification event in one step.
     */
    public void publishNotificationEvent(EventType eventType, Long recipientId, Long senderId, 
                                        String message, String entityType, Long entityId) {
        SocialEvent event = new SocialEvent(eventType.name())
                .addData("recipientId", recipientId)
                .addData("senderId", senderId)
                .addData("message", message)
                .addData("entityType", entityType)
                .addData("entityId", entityId);
        
        publishEvent(event);
    }
} 