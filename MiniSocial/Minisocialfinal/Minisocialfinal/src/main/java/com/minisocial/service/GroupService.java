package com.minisocial.service;

import com.minisocial.dto.GroupDTO;
import com.minisocial.dto.GroupMembershipRequestDTO;
import com.minisocial.dto.UserDTO;
import com.minisocial.entity.Group;
import com.minisocial.entity.GroupMembershipRequest;
import com.minisocial.entity.User;
import com.minisocial.util.JPAUtil;
import com.minisocial.util.NotificationManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GroupService {
    
    @Transactional
    public GroupDTO createGroup(String name, String description, boolean isOpen, Long creatorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User creator = em.find(User.class, creatorId);
            if (creator == null) {
                throw new IllegalArgumentException("Creator not found");
            }

            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setCreator(creator);
            group.setOpen(isOpen);
            group.getMembers().add(creator);
            group.getAdmins().add(creator);

            em.persist(group);
            
            // Notify creator they've joined (created) a new group
            NotificationManager.sendGroupJoinedNotification(creatorId, group.getId());
            
            return convertToDTO(group);
        } finally {
            em.close();
        }
    }

    public GroupDTO getGroup(Long groupId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            if (group == null) {
                throw new IllegalArgumentException("Group not found");
            }
            return convertToDTO(group);
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupMembershipRequestDTO requestJoinGroup(Long groupId, Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            User user = em.find(User.class, userId);

            if (group == null || user == null) {
                throw new IllegalArgumentException("Group or user not found");
            }

            if (group.getMembers().contains(user)) {
                throw new IllegalStateException("User is already a member of the group");
            }

            // Check if there's already a pending request
            try {
                em.createQuery("SELECT r FROM GroupMembershipRequest r WHERE r.group = :group AND r.user = :user AND r.status = 'PENDING'", GroupMembershipRequest.class)
                  .setParameter("group", group)
                  .setParameter("user", user)
                  .getSingleResult();
                throw new IllegalStateException("User already has a pending request for this group");
            } catch (NoResultException e) {
                // No pending request exists, continue
            }

            GroupMembershipRequest request = new GroupMembershipRequest();
            request.setGroup(group);
            request.setUser(user);
            request.setStatus(GroupMembershipRequest.RequestStatus.PENDING);

            em.persist(request);
            return convertToDTO(request);
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupDTO approveJoinRequest(Long requestId, Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            GroupMembershipRequest request = em.find(GroupMembershipRequest.class, requestId);
            User admin = em.find(User.class, adminId);

            if (request == null || admin == null) {
                throw new IllegalArgumentException("Request or admin not found");
            }

            if (!request.getGroup().getAdmins().contains(admin)) {
                throw new IllegalStateException("User is not an admin of the group");
            }

            if (request.getStatus() != GroupMembershipRequest.RequestStatus.PENDING) {
                throw new IllegalStateException("Request is not pending");
            }

            request.setStatus(GroupMembershipRequest.RequestStatus.APPROVED);
            request.getGroup().getMembers().add(request.getUser());

            em.merge(request);
            
            // Send notification to user that their join request was approved
            NotificationManager.sendGroupJoinedNotification(request.getUser().getId(), request.getGroup().getId());
            
            return convertToDTO(request.getGroup());
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupDTO rejectJoinRequest(Long requestId, Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            GroupMembershipRequest request = em.find(GroupMembershipRequest.class, requestId);
            User admin = em.find(User.class, adminId);

            if (request == null || admin == null) {
                throw new IllegalArgumentException("Request or admin not found");
            }

            if (!request.getGroup().getAdmins().contains(admin)) {
                throw new IllegalStateException("User is not an admin of the group");
            }

            request.setStatus(GroupMembershipRequest.RequestStatus.REJECTED);
            return convertToDTO(request.getGroup());
        } finally {
            em.close();
        }
    }

    @Transactional
    public GroupDTO promoteToAdmin(Long groupId, Long userId, Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            User user = em.find(User.class, userId);
            User admin = em.find(User.class, adminId);

            if (group == null || user == null || admin == null) {
                throw new IllegalArgumentException("Group, user, or admin not found");
            }

            if (!group.getAdmins().contains(admin)) {
                throw new IllegalStateException("User is not an admin of the group");
            }

            if (!group.getMembers().contains(user)) {
                throw new IllegalStateException("User is not a member of the group");
            }

            group.getAdmins().add(user);
            return convertToDTO(group);
        } finally {
            em.close();
        }
    }

    @Transactional
    public void removeMember(Long groupId, Long memberId, Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            User member = em.find(User.class, memberId);
            User admin = em.find(User.class, adminId);

            if (group == null || member == null || admin == null) {
                throw new IllegalArgumentException("Group, member, or admin not found");
            }

            if (!group.getAdmins().contains(admin)) {
                throw new IllegalStateException("User is not an admin of the group");
            }

            if (!group.getMembers().contains(member)) {
                throw new IllegalStateException("User is not a member of the group");
            }

            if (group.getCreator().equals(member)) {
                throw new IllegalStateException("Cannot remove the group creator");
            }

            group.getMembers().remove(member);
            group.getAdmins().remove(member);
            
            // Send notification to removed member
            NotificationManager.sendGroupLeftNotification(memberId, groupId);
            
            em.merge(group);
        } finally {
            em.close();
        }
    }
    
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
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

            if (group.getCreator().equals(user)) {
                throw new IllegalStateException("Group creator cannot leave the group");
            }

            group.getMembers().remove(user);
            group.getAdmins().remove(user);
            
            // Send notification to user that they left the group
            NotificationManager.sendGroupLeftNotification(userId, groupId);
            
            em.merge(group);
        } finally {
            em.close();
        }
    }

    @Transactional
    public void deleteGroup(Long groupId, Long adminId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Group group = em.find(Group.class, groupId);
            User admin = em.find(User.class, adminId);

            if (group == null || admin == null) {
                throw new IllegalArgumentException("Group or admin not found");
            }

            if (!group.getCreator().equals(admin)) {
                throw new IllegalStateException("Only the group creator can delete the group");
            }
            
            // Notify all members that the group has been deleted (they've left the group)
            for (User member : group.getMembers()) {
                if (!member.equals(admin)) { // Don't notify the admin who deleted the group
                    NotificationManager.sendGroupLeftNotification(member.getId(), groupId);
                }
            }

            em.remove(group);
        } finally {
            em.close();
        }
    }

    public List<GroupDTO> getGroupsByMember(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            List<Group> groups = em.createQuery("SELECT g FROM Group g JOIN g.members m WHERE m = :user", Group.class)
                                 .setParameter("user", user)
                                 .getResultList();

            return groups.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<GroupDTO> getGroupsByAdmin(Long userId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            List<Group> groups = em.createQuery("SELECT g FROM Group g JOIN g.admins a WHERE a = :user", Group.class)
                                 .setParameter("user", user)
                                 .getResultList();

            return groups.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    private GroupDTO convertToDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreator(convertToDTO(group.getCreator()));
        dto.setMembers(group.getMembers().stream()
                           .map(this::convertToDTO)
                           .collect(Collectors.toSet()));
        dto.setAdmins(group.getAdmins().stream()
                          .map(this::convertToDTO)
                          .collect(Collectors.toSet()));
        dto.setOpen(group.isOpen());
        dto.setCreatedAt(group.getCreatedAt());
        return dto;
    }

    private GroupMembershipRequestDTO convertToDTO(GroupMembershipRequest request) {
        GroupMembershipRequestDTO dto = new GroupMembershipRequestDTO();
        dto.setId(request.getId());
        dto.setGroup(convertToDTO(request.getGroup()));
        dto.setUser(convertToDTO(request.getUser()));
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
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