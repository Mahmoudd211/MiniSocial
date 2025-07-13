package com.minisocial.util;

import java.util.logging.Logger;

/**
 * Utility class to handle security context and authentication information.
 */
public class SecurityContext {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityContext.class.getName());
    private static ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    /**
     * Set the current user ID for this thread.
     * This is called by the JwtAuthenticationFilter after successful token validation.
     * 
     * @param userId The ID of the authenticated user
     */
    public static void setCurrentUserId(Long userId) {
        LOGGER.info("Setting current user ID: " + userId);
        currentUserId.set(userId);
    }
    
    /**
     * Get the current authenticated user ID.
     * 
     * @return The current user ID or null if not authenticated
     */
    public static Long getCurrentUserId() {
        Long userId = currentUserId.get();
        LOGGER.info("Getting current user ID: " + userId);
        return userId;
    }
    
    /**
     * Clear the current user ID when the request is complete.
     * This should be called in a filter's doFilter method after the chain.
     */
    public static void clearCurrentUserId() {
        LOGGER.info("Clearing current user ID");
        currentUserId.remove();
    }
} 