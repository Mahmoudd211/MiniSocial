package com.minisocial.service;

import com.minisocial.dto.LikeDTO;
import com.minisocial.entity.Like;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import com.minisocial.util.NotificationManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LikeService {
    public LikeDTO likePost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Post post = em.find(Post.class, postId);
            if (post == null) {
                throw new IllegalArgumentException("Post not found");
            }

            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            // Check if user already liked the post
            List<Like> existingLikes = em.createNamedQuery("Like.findByUserAndPost", Like.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId)
                    .getResultList();

            if (!existingLikes.isEmpty()) {
                throw new IllegalArgumentException("User has already liked this post");
            }

            Like like = new Like();
            like.setPost(post);
            like.setUser(user);

            em.getTransaction().begin();
            em.persist(like);
            em.getTransaction().commit();
            
            // Send notification to post author about the like
            NotificationManager.sendPostLikedNotification(postId, userId);
            
            return new LikeDTO(like);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void unlikePost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Like> likes = em.createNamedQuery("Like.findByUserAndPost", Like.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId)
                    .getResultList();

            if (likes.isEmpty()) {
                throw new IllegalArgumentException("User has not liked this post");
            }

            em.getTransaction().begin();
            em.remove(likes.get(0));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<LikeDTO> getPostLikes(Long postId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Like.findByPost", Like.class)
                    .setParameter("postId", postId)
                    .getResultList()
                    .stream()
                    .map(LikeDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public boolean hasUserLikedPost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return !em.createNamedQuery("Like.findByUserAndPost", Like.class)
                    .setParameter("userId", userId)
                    .setParameter("postId", postId)
                    .getResultList()
                    .isEmpty();
        } finally {
            em.close();
        }
    }
} 