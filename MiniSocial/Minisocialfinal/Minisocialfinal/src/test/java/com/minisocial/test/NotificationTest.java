package com.minisocial.test;

import com.minisocial.entity.User;
import com.minisocial.entity.Post;
import com.minisocial.entity.Comment;
import com.minisocial.entity.Group;
import com.minisocial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {
    
    @Test
    public void testPostLikeNotification() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Create two users
            User author = new User();
            author.setName("Test Author");
            author.setEmail("author@test.com");
            author.setPassword("password");
            
            User liker = new User();
            liker.setName("Test Liker");
            liker.setEmail("liker@test.com");
            liker.setPassword("password");
            
            em.getTransaction().begin();
            em.persist(author);
            em.persist(liker);
            em.getTransaction().commit();
            
            // Create a post
            Post post = new Post();
            post.setContent("Test post content");
            post.setAuthor(author);
            
            em.getTransaction().begin();
            em.persist(post);
            em.getTransaction().commit();
            
            // Like the post
            em.getTransaction().begin();
            post.getLikes().add(liker);
            em.merge(post);
            em.getTransaction().commit();
            
            // Verify notification was created
            em.clear();
            User refreshedAuthor = em.find(User.class, author.getId());
            assertNotNull(refreshedAuthor);
            assertFalse(refreshedAuthor.getNotifications().isEmpty());
            
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testCommentNotification() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Create two users
            User author = new User();
            author.setName("Test Author");
            author.setEmail("author2@test.com");
            author.setPassword("password");
            
            User commenter = new User();
            commenter.setName("Test Commenter");
            commenter.setEmail("commenter@test.com");
            commenter.setPassword("password");
            
            em.getTransaction().begin();
            em.persist(author);
            em.persist(commenter);
            em.getTransaction().commit();
            
            // Create a post
            Post post = new Post();
            post.setContent("Test post content");
            post.setAuthor(author);
            
            em.getTransaction().begin();
            em.persist(post);
            em.getTransaction().commit();
            
            // Add a comment
            Comment comment = new Comment();
            comment.setContent("Test comment");
            comment.setAuthor(commenter);
            comment.setPost(post);
            
            em.getTransaction().begin();
            em.persist(comment);
            em.getTransaction().commit();
            
            // Verify notification was created
            em.clear();
            User refreshedAuthor = em.find(User.class, author.getId());
            assertNotNull(refreshedAuthor);
            assertFalse(refreshedAuthor.getNotifications().isEmpty());
            
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGroupJoinNotification() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Create users
            User creator = new User();
            creator.setName("Group Creator");
            creator.setEmail("creator@test.com");
            creator.setPassword("password");
            
            User joiner = new User();
            joiner.setName("Group Joiner");
            joiner.setEmail("joiner@test.com");
            joiner.setPassword("password");
            
            em.getTransaction().begin();
            em.persist(creator);
            em.persist(joiner);
            em.getTransaction().commit();
            
            // Create a group
            Group group = new Group();
            group.setName("Test Group");
            group.setDescription("Test Description");
            group.setCreator(creator);
            group.setOpen(true);
            
            em.getTransaction().begin();
            em.persist(group);
            em.getTransaction().commit();
            
            // Join the group
            em.getTransaction().begin();
            group.getMembers().add(joiner);
            em.merge(group);
            em.getTransaction().commit();
            
            // Verify notification was created
            em.clear();
            User refreshedJoiner = em.find(User.class, joiner.getId());
            assertNotNull(refreshedJoiner);
            assertFalse(refreshedJoiner.getNotifications().isEmpty());
            
        } finally {
            em.close();
        }
    }
} 