package com.minisocial.jms;

import com.minisocial.dto.NotificationEvent;
import com.minisocial.entity.Notification;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.persistence.EntityManager;
import java.util.logging.Logger;

@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
    }
)
public class NotificationListener implements MessageListener {
    
    private static final Logger logger = Logger.getLogger(NotificationListener.class.getName());

    @Override
    public void onMessage(Message message) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                NotificationEvent event = (NotificationEvent) objectMessage.getObject();
                
                // Log the notification event
                logger.info("Received notification: " + formatNotification(event));
                
                // Store the notification in the database
                Notification notification = new Notification();
                notification.setEventType(event.getEventType());
                notification.setRecipient(em.find(User.class, event.getRecipientId()));
                if (event.getSenderId() != null) {
                    notification.setSender(em.find(User.class, event.getSenderId()));
                }
                notification.setMessage(event.getMessage());
                notification.setEntityType(event.getEntityType());
                notification.setEntityId(event.getEntityId());
                notification.setCreatedAt(event.getCreatedAt());
                notification.setRead(event.isRead());

                em.persist(notification);
                
            } else {
                logger.warning("Received message of wrong type: " + message.getClass().getName());
            }
        } catch (Exception e) {
            logger.severe("Error processing notification message: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private String formatNotification(NotificationEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event Type: ").append(event.getEventType())
          .append(", Recipient: ").append(event.getRecipientId())
          .append(", Sender: ").append(event.getSenderId())
          .append(", Message: ").append(event.getMessage())
          .append(", Entity Type: ").append(event.getEntityType())
          .append(", Entity ID: ").append(event.getEntityId())
          .append(", Created At: ").append(event.getCreatedAt());
        return sb.toString();
    }
} 