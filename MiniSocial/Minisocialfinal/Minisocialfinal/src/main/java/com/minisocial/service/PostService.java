package com.minisocial.service;

import com.minisocial.dto.PostDTO;
import com.minisocial.entity.Post;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

public class PostService {

    public PostDTO createPost(Long userId, PostDTO postDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User author = em.find(User.class, userId);
            if (author == null) {
                throw new IllegalArgumentException("User not found");
            }

            Post post = new Post();
            post.setAuthor(author);
            post.setContent(postDTO.getContent());
            post.setImageUrls(postDTO.getImageUrls());
            post.setLinkUrls(postDTO.getLinkUrls());

            em.getTransaction().begin();
            em.persist(post);
            em.getTransaction().commit();

            return new PostDTO(post);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public PostDTO updatePost(Long postId, Long userId, PostDTO postDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Post post = em.find(Post.class, postId);
            if (post == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (!post.getAuthor().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to update this post");
            }

            post.setContent(postDTO.getContent());
            post.setImageUrls(postDTO.getImageUrls());
            post.setLinkUrls(postDTO.getLinkUrls());
            post.setUpdatedAt(java.time.LocalDateTime.now());

            em.getTransaction().begin();
            em.merge(post);
            em.getTransaction().commit();

            return new PostDTO(post);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deletePost(Long postId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Post post = em.find(Post.class, postId);
            if (post == null) {
                throw new IllegalArgumentException("Post not found");
            }

            if (!post.getAuthor().getId().equals(userId)) {
                throw new IllegalArgumentException("User is not authorized to delete this post");
            }

            em.getTransaction().begin();
            em.remove(post);
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

    public List<PostDTO> getUserPosts(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Post> query = em.createNamedQuery("Post.findByUser", Post.class);
            query.setParameter("userId", userId);
            
            return query.getResultList().stream()
                    .map(PostDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PostDTO> getFriendsPosts(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Post> query = em.createNamedQuery("Post.findByFriends", Post.class);
            query.setParameter("userId", userId);
            
            return query.getResultList().stream()
                    .map(PostDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public PostDTO getPostById(Long postId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Post> query = em.createNamedQuery("Post.findById", Post.class);
            query.setParameter("postId", postId);
            
            Post post = query.getResultList().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Post not found"));
            
            return new PostDTO(post);
        } finally {
            em.close();
        }
    }
} 