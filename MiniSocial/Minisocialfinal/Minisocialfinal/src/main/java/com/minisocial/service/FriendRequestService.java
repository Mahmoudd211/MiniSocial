package com.minisocial.service;

import com.minisocial.dto.UserDTO;
import com.minisocial.entity.FriendRequest;
import com.minisocial.entity.User;
import com.minisocial.dto.FriendRequestDTO;
import com.minisocial.event.EventType;
import com.minisocial.event.NotificationEventService;
import com.minisocial.event.SocialEvent;
import com.minisocial.util.JPAUtil;
import com.minisocial.util.NotificationManager;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FriendRequestService {

    @Inject
    private NotificationEventService eventService;

    public FriendRequestDTO sendFriendRequest(Long senderId, Long receiverId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Check if users exist
            User sender = em.find(User.class, senderId);
            User receiver = em.find(User.class, receiverId);
            
            if (sender == null || receiver == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Check if request already exists
            TypedQuery<FriendRequest> query = em.createNamedQuery("FriendRequest.findBySenderAndReceiver", FriendRequest.class);
            query.setParameter("senderId", senderId);
            query.setParameter("receiverId", receiverId);
            
            if (!query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("Friend request already exists");
            }

            // Create new friend request
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);

            em.getTransaction().begin();
            em.persist(friendRequest);
            em.getTransaction().commit();

            // Send notification using the event system
            if (eventService != null) {
                // Create a standardized event
                SocialEvent event = new SocialEvent(EventType.FRIEND_REQUEST_SENT.name())
                    .addData("recipientId", receiverId)
                    .addData("senderId", senderId)
                    .addData("message", sender.getName() + " sent you a friend request")
                    .addData("entityType", "FRIEND_REQUEST")
                    .addData("entityId", senderId);
                
                // Publish the event
                eventService.publishEvent(event);
            } else {
                // Fallback to direct notification if event service is not available
                NotificationManager.sendFriendRequestNotification(receiverId, senderId);
            }

            return new FriendRequestDTO(friendRequest);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public FriendRequestDTO respondToFriendRequest(Long requestId, Long userId, boolean accept) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            FriendRequest friendRequest = em.find(FriendRequest.class, requestId);
            
            if (friendRequest == null) {
                throw new IllegalArgumentException("Friend request not found");
            }

            if (!friendRequest.getReceiver().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to respond to this request");
            }

            if (friendRequest.getStatus() != FriendRequest.RequestStatus.PENDING) {
                throw new IllegalArgumentException("Friend request is not pending");
            }

            friendRequest.setStatus(accept ? FriendRequest.RequestStatus.ACCEPTED : FriendRequest.RequestStatus.REJECTED);

            em.getTransaction().begin();
            em.merge(friendRequest);
            em.getTransaction().commit();
            
            // If the request was accepted, publish a friend request accepted event
            if (accept && eventService != null) {
                Long senderId = friendRequest.getSender().getId();
                
                // Notify the sender that their request was accepted
                SocialEvent event = new SocialEvent(EventType.FRIEND_REQUEST_ACCEPTED.name())
                    .addData("recipientId", senderId)
                    .addData("senderId", userId)
                    .addData("message", friendRequest.getReceiver().getName() + " accepted your friend request")
                    .addData("entityType", "FRIEND_REQUEST")
                    .addData("entityId", userId);
                
                eventService.publishEvent(event);
            }

            return new FriendRequestDTO(friendRequest);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<FriendRequestDTO> getPendingFriendRequests(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<FriendRequest> query = em.createNamedQuery("FriendRequest.findPendingByReceiver", FriendRequest.class);
            query.setParameter("receiverId", userId);
            
            return query.getResultList().stream()
                    .map(FriendRequestDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getFriends(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<FriendRequest> query = em.createNamedQuery("FriendRequest.findFriendsByUser", FriendRequest.class);
            query.setParameter("userId", userId);
            
            return query.getResultList().stream()
                    .map(fr -> {
                        User friend = fr.getSender().getId().equals(userId) ? fr.getReceiver() : fr.getSender();
                        return new UserDTO(friend);
                    })
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
} 