package com.minisocial.service;

import com.minisocial.dto.CommentDTO;
import com.minisocial.entity.Comment;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import com.minisocial.util.NotificationManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentService {
    public CommentDTO createComment(Long postId, Long userId, CommentDTO commentDTO) {
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

            Comment comment = new Comment();
            comment.setPost(post);
            comment.setAuthor(user);
            comment.setContent(commentDTO.getContent());

            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
            
            // Send notification to post author about the comment
            NotificationManager.sendPostCommentedNotification(postId, userId);
            
            return new CommentDTO(comment);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public CommentDTO updateComment(Long commentId, Long userId, CommentDTO commentDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Comment comment = em.find(Comment.class, commentId);
            if (comment == null) {
                throw new IllegalArgumentException("Comment not found");
            }

            if (!comment.getAuthor().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to update this comment");
            }

            comment.setContent(commentDTO.getContent());
            comment.setUpdatedAt(java.time.LocalDateTime.now());

            em.getTransaction().begin();
            em.merge(comment);
            em.getTransaction().commit();
            return new CommentDTO(comment);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteComment(Long commentId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Comment comment = em.find(Comment.class, commentId);
            if (comment == null) {
                throw new IllegalArgumentException("Comment not found");
            }

            if (!comment.getAuthor().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to delete this comment");
            }

            em.getTransaction().begin();
            em.remove(comment);
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

    public List<CommentDTO> getPostComments(Long postId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createNamedQuery("Comment.findByPost", Comment.class)
                    .setParameter("postId", postId)
                    .getResultList()
                    .stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
} 