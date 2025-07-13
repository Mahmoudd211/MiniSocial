package com.minisocial.rest;

import com.minisocial.dto.NotificationDTO;
import com.minisocial.entity.Notification;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationResource.class.getName());

    @Context
    private SecurityContext securityContext;

    public NotificationResource() {
        // Default constructor
    }

    /**
     * Get the current user's notifications
     */
    @GET
    public Response getCurrentUserNotifications(@QueryParam("userId") Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                LOGGER.warning("Security context or user principal is null");
                return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Bearer realm=\"MiniSocial\"")
                    .entity("User is not authenticated. Please login and provide a valid JWT token.")
                    .build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                LOGGER.warning("User not found for email: " + email);
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found")
                    .build();
            }

            Long currentUserId = user.getId();
            LOGGER.info("Current user ID: " + currentUserId);

            // If userId is provided, verify it matches the current user
            if (userId != null && !userId.equals(currentUserId)) {
                LOGGER.warning("User " + currentUserId + " attempted to access notifications for user " + userId);
                return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can only view your own notifications")
                    .build();
            }

            // Use currentUserId if no specific userId was provided
            Long targetUserId = userId != null ? userId : currentUserId;
            List<NotificationDTO> notifications = getNotifications(em, targetUserId, false);
            return Response.ok(notifications).build();
        } catch (Exception e) {
            LOGGER.severe("Error getting notifications: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving notifications: " + e.getMessage())
                .build();
        } finally {
            em.close();
        }
    }

    /**
     * Get the current user's unread notifications
     */
    @GET
    @Path("/unread")
    public Response getUnreadNotifications() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                LOGGER.warning("Security context or user principal is null");
                return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Bearer realm=\"MiniSocial\"")
                    .entity("User is not authenticated. Please login and provide a valid JWT token.")
                    .build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                LOGGER.warning("User not found for email: " + email);
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found")
                    .build();
            }

            Long currentUserId = user.getId();
            LOGGER.info("Getting unread notifications for user: " + currentUserId);
            
            List<NotificationDTO> notifications = getNotifications(em, currentUserId, true);
            LOGGER.info("Retrieved " + notifications.size() + " unread notifications for user " + currentUserId);
            
            return Response.ok(notifications).build();
        } catch (Exception e) {
            LOGGER.severe("Error getting unread notifications: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error retrieving unread notifications: " + e.getMessage())
                .build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserNotifications(@PathParam("userId") Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authenticated").build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
            }

            Long currentUserId = user.getId();

            // Only allow users to view their own notifications
            if (!userId.equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Cannot view other users' notifications").build();
            }

            List<NotificationDTO> notifications = getNotifications(em, userId, false);
            return Response.ok(notifications).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @GET
    @Path("/user/{userId}/unread")
    public Response getUserUnreadNotifications(@PathParam("userId") Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authenticated").build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
            }

            Long currentUserId = user.getId();

            // Only allow users to view their own notifications
            if (!userId.equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Cannot view other users' notifications").build();
            }

            List<NotificationDTO> notifications = getNotifications(em, userId, true);
            return Response.ok(notifications).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } finally {
            em.close();
        }
    }

    @PUT
    @Path("/{notificationId}/read")
    public Response markAsRead(@PathParam("notificationId") Long notificationId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authenticated").build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
            }

            Long currentUserId = user.getId();

            // Check if the notification belongs to the current user
            Notification notification = em.find(Notification.class, notificationId);
            if (notification == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Notification not found").build();
            }

            if (!notification.getRecipient().getId().equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Cannot modify other users' notifications").build();
            }

            em.getTransaction().begin();
            notification.setRead(true);
            em.merge(notification);
            em.getTransaction().commit();
            return Response.noContent().build();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error marking notification as read: " + e.getMessage())
                .build();
        } finally {
            em.close();
        }
    }

    /**
     * Mark all of the current user's notifications as read
     */
    @PUT
    @Path("/read-all")
    public Response markAllAsRead() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authenticated").build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
            }

            Long currentUserId = user.getId();

            em.getTransaction().begin();
            int updated = em.createQuery("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :userId AND n.read = false")
                    .setParameter("userId", currentUserId)
                    .executeUpdate();
            em.getTransaction().commit();
            
            return Response.ok(updated + " notifications marked as read").build();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error marking all notifications as read: " + e.getMessage())
                .build();
        } finally {
            em.close();
        }
    }

    @PUT
    @Path("/user/{userId}/read-all")
    public Response markAllAsRead(@PathParam("userId") Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            if (securityContext == null || securityContext.getUserPrincipal() == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User is not authenticated").build();
            }

            String email = securityContext.getUserPrincipal().getName();
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                         .setParameter("email", email)
                         .getSingleResult();
            
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
            }

            Long currentUserId = user.getId();

            // Only allow users to mark their own notifications as read
            if (!userId.equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("Cannot modify other users' notifications").build();
            }

            em.getTransaction().begin();
            int updated = em.createQuery("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :userId AND n.read = false")
                    .setParameter("userId", currentUserId)
                    .executeUpdate();
            em.getTransaction().commit();
            
            return Response.ok(updated + " notifications marked as read").build();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error marking all notifications as read: " + e.getMessage())
                .build();
        } finally {
            em.close();
        }
    }

    private List<NotificationDTO> getNotifications(EntityManager em, Long userId, boolean unreadOnly) {
        String query = "SELECT n FROM Notification n WHERE n.recipient.id = :userId";
        if (unreadOnly) {
            query += " AND n.read = false";
        }
        query += " ORDER BY n.createdAt DESC";
        
        return em.createQuery(query, Notification.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
} 