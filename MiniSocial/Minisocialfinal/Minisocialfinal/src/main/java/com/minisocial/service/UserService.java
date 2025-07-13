package com.minisocial.service;

import com.minisocial.entity.User;
import com.minisocial.dto.UserDTO;
import com.minisocial.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    public UserDTO registerUser(UserDTO userDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Check if email already exists
            TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
            query.setParameter("email", userDTO.getEmail());
            
            if (!query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("Email already registered");
            }

            User user = new User();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword()); // In production, this should be hashed
            user.setBio(userDTO.getBio());
            user.setRole(userDTO.getRole() != null ? userDTO.getRole() : User.UserRole.USER);

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public UserDTO login(String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
            query.setParameter("email", email);
            
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }

            User user = users.get(0);
            if (!user.getPassword().equals(password)) { // In production, use proper password comparison
                throw new IllegalArgumentException("Invalid password");
            }

            return new UserDTO(user);
        } finally {
            em.close();
        }
    }

    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            if (userDTO.getName() != null) {
                user.setName(userDTO.getName());
            }
            if (userDTO.getBio() != null) {
                user.setBio(userDTO.getBio());
            }
            if (userDTO.getPassword() != null) {
                user.setPassword(userDTO.getPassword()); // In production, this should be hashed
            }

            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<UserDTO> getAllUsers() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);
            return query.getResultList().stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public UserDTO getUserById(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return new UserDTO(user);
        } finally {
            em.close();
        }
    }

    public User getUserByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email",
                User.class
            );
            query.setParameter("email", email);
            return query.getResultList().stream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            if (userDTO.getName() != null) {
                user.setName(userDTO.getName());
            }
            if (userDTO.getBio() != null) {
                user.setBio(userDTO.getBio());
            }
            if (userDTO.getPassword() != null) {
                user.setPassword(userDTO.getPassword()); // In production, hash the password
            }
            if (userDTO.getRole() != null) {
                user.setRole(userDTO.getRole());
            }
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public User authenticateUser(String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
            query.setParameter("email", email);
            List<User> users = query.getResultList();
            if (users.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }
            User user = users.get(0);
            if (!user.getPassword().equals(password)) { // In production, use proper password comparison
                throw new IllegalArgumentException("Invalid password");
            }
            return user;
        } finally {
            em.close();
        }
    }

    public void deleteUser(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            em.getTransaction().begin();
            em.remove(user);
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
}