package com.minisocial.service;

import com.minisocial.dto.GroupPostDTO;
import com.minisocial.dto.GroupPostCommentDTO;
import com.minisocial.dto.GroupPostLikeDTO;
import com.minisocial.dto.UserDTO;
import com.minisocial.entity.Group;
import com.minisocial.entity.GroupPost;
import com.minisocial.entity.GroupPostComment;
import com.minisocial.entity.GroupPostLike;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import com.minisocial.util.NotificationManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GroupPostService {
    
    @Transactional
    public GroupPostDTO createPost(Long groupId, Long userId, String content) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            User user = em.find(User.class, userId);

            if (group == null || user == null) {
                throw new IllegalArgumentException("Group or user not found");
            }

            if (!group.getMembers().contains(user)) {
                throw new IllegalStateException("User is not a member of the group");
            }

            GroupPost post = new GroupPost();
            post.setGroup(group);
            post.setAuthor(user);
            post.setContent(content);

            em.persist(post);
            return convertToDTO(post);
        } finally {
            em.close();
        }
    }

    public List<GroupPostDTO> getGroupPosts(Long groupId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            if (group == null) {
                throw new IllegalArgumentException("Group not found");
            }

            List<GroupPost> posts = em.createQuery("SELECT p FROM GroupPost p WHERE p.group = :group ORDER BY p.createdAt DESC", GroupPost.class)
                                    .setParameter("group", group)
                                    .getResultList();

            return posts.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupPostCommentDTO addComment(Long postId, Long userId, String content) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            GroupPost post = em.find(GroupPost.class, postId);
            User user = em.find(User.class, userId);

            if (post == null || user == null) {
                throw new IllegalArgumentException("Post or user not found");
            }

            if (!post.getGroup().getMembers().contains(user)) {
                throw new IllegalStateException("User is not a member of the group");
            }

            GroupPostComment comment = new GroupPostComment();
            comment.setPost(post);
            comment.setAuthor(user);
            comment.setContent(content);

            em.persist(comment);
            
            // Send notification to post author if it's not the same user
            if (!post.getAuthor().getId().equals(userId)) {
                try {
                    NotificationManager.sendPostCommentedNotification(post.getId(), userId);
                } catch (Exception e) {
                    // Log the error but don't fail the comment creation
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
            
            return convertToDTO(comment);
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupPostLikeDTO likePost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            GroupPost post = em.find(GroupPost.class, postId);
            User user = em.find(User.class, userId);

            if (post == null || user == null) {
                throw new IllegalArgumentException("Post or user not found");
            }

            if (!post.getGroup().getMembers().contains(user)) {
                throw new IllegalStateException("User is not a member of the group");
            }

            // Check if user already liked the post
            try {
                em.createQuery("SELECT l FROM GroupPostLike l WHERE l.post = :post AND l.user = :user", GroupPostLike.class)
                  .setParameter("post", post)
                  .setParameter("user", user)
                  .getSingleResult();
                throw new IllegalStateException("User already liked this post");
            } catch (jakarta.persistence.NoResultException e) {
                // No existing like found, continue
            }

            GroupPostLike like = new GroupPostLike();
            like.setPost(post);
            like.setUser(user);

            em.persist(like);
            
            // Send notification to post author if it's not the same user
            if (!post.getAuthor().getId().equals(userId)) {
                try {
                    NotificationManager.sendPostLikedNotification(post.getId(), userId);
                } catch (Exception e) {
                    // Log the error but don't fail the like creation
                    System.err.println("Failed to send notification: " + e.getMessage());
                }
            }
            
            return convertToDTO(like);
        } finally {
            em.close();
        }
    }

    @Transactional
    public void removePost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            GroupPost post = em.find(GroupPost.class, postId);
            User user = em.find(User.class, userId);

            if (post == null || user == null) {
                throw new IllegalArgumentException("Post or user not found");
            }

            if (!post.getAuthor().equals(user) && !post.getGroup().getAdmins().contains(user)) {
                throw new IllegalStateException("User is not authorized to remove this post");
            }

            em.remove(post);
        } finally {
            em.close();
        }
    }

    private GroupPostDTO convertToDTO(GroupPost post) {
        GroupPostDTO dto = new GroupPostDTO();
        dto.setId(post.getId());
        dto.setGroup(convertToDTO(post.getGroup()));
        dto.setAuthor(convertToDTO(post.getAuthor()));
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setComments(post.getComments().stream()
                           .map(this::convertToDTO)
                           .collect(Collectors.toSet()));
        dto.setLikes(post.getLikes().stream()
                         .map(this::convertToDTO)
                         .collect(Collectors.toSet()));
        return dto;
    }

    private GroupPostCommentDTO convertToDTO(GroupPostComment comment) {
        GroupPostCommentDTO dto = new GroupPostCommentDTO();
        dto.setId(comment.getId());
        dto.setPost(convertToDTO(comment.getPost()));
        dto.setAuthor(convertToDTO(comment.getAuthor()));
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    private GroupPostLikeDTO convertToDTO(GroupPostLike like) {
        GroupPostLikeDTO dto = new GroupPostLikeDTO();
        dto.setId(like.getId());
        dto.setPost(convertToDTO(like.getPost()));
        dto.setUser(convertToDTO(like.getUser()));
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }

    private com.minisocial.dto.GroupDTO convertToDTO(Group group) {
        com.minisocial.dto.GroupDTO dto = new com.minisocial.dto.GroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setOpen(group.isOpen());
        dto.setCreatedAt(group.getCreatedAt());
        return dto;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
} 