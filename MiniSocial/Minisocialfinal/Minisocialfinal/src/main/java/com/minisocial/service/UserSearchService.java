package com.minisocial.service;

import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

public class UserSearchService {
    
    public List<User> searchUsers(String query) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> searchQuery = em.createQuery(
                "SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(:query) OR LOWER(u.email) LIKE LOWER(:query)",
                User.class
            );
            searchQuery.setParameter("query", "%" + query + "%");
            return searchQuery.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<User> getFriendSuggestions(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {

            User user = em.find(User.class, userId);
            if (user == null) {
                return Collections.emptyList();
            }
            
            // Get user's friends
            Set<User> friends = user.getFriends();
            
            // Get friends of friends
            Set<User> friendsOfFriends = new HashSet<>();
            for (User friend : friends) {
                friendsOfFriends.addAll(friend.getFriends());
            }
            
            // Remove the user and their direct friends
            friendsOfFriends.remove(user);
            friendsOfFriends.removeAll(friends);
            
            // Calculate mutual friend count for each suggestion
            Map<User, Long> mutualFriendsCount = new HashMap<>();
            for (User potentialFriend : friendsOfFriends) {
                long mutualCount = potentialFriend.getFriends().stream()
                    .filter(friends::contains)
                    .count();
                mutualFriendsCount.put(potentialFriend, mutualCount);
            }
            
            // Sort by number of mutual friends
            return mutualFriendsCount.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
        } finally {
            em.close();
        }
    }
} 