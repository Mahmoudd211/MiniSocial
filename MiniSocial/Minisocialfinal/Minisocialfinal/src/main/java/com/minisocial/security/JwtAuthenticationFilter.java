package com.minisocial.security;

import com.minisocial.entity.User;
import com.minisocial.service.UserService;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private static final String REALM = "MiniSocial";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    
    @jakarta.inject.Inject
    private JwtTokenProvider tokenProvider;
    
    @jakarta.inject.Inject
    private UserService userService;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        LOGGER.info("Processing request for path: " + path);
        
        // Skip authentication for login and registration endpoints
        if (isPublicEndpoint(requestContext)) {
            LOGGER.info("Skipping authentication for public endpoint: " + path);
            return;
        }
        
        // Get the Authorization header
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        LOGGER.info("Authorization header: " + (authorizationHeader != null ? "present" : "missing"));
        
        // Validate the Authorization header
        if (authorizationHeader == null) {
            LOGGER.warning("Missing Authorization header for protected endpoint: " + path);
            abortWithUnauthorized(requestContext, "Missing Authorization header");
            return;
        }
        
        if (!authorizationHeader.startsWith(AUTHENTICATION_SCHEME + " ")) {
            LOGGER.warning("Invalid Authorization header format for endpoint: " + path);
            abortWithUnauthorized(requestContext, "Invalid Authorization header format. Must be: Bearer <token>");
            return;
        }
        
        // Extract the token
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        LOGGER.info("Extracted token for endpoint: " + path);
        
        try {
            // Validate the token
            if (!tokenProvider.validateToken(token)) {
                LOGGER.warning("Invalid token for endpoint: " + path);
                abortWithUnauthorized(requestContext, "Invalid token");
                return;
            }
            
            // Get user info from token
            String username = tokenProvider.getUsername(token);
            String role = tokenProvider.getRole(token);
            LOGGER.info("Token validated for user: " + username + " with role: " + role + " accessing endpoint: " + path);
            
            // Get user ID from username (email)
            User user = userService.getUserByEmail(username);
            if (user == null) {
                LOGGER.warning("User not found for email: " + username + " accessing endpoint: " + path);
                abortWithUnauthorized(requestContext, "User not found");
                return;
            }
            
            // Set the user ID in the security context
            com.minisocial.util.SecurityContext.setCurrentUserId(user.getId());
            LOGGER.info("Set current user ID: " + user.getId() + " for endpoint: " + path);
            
            // Set security context
            final String finalUsername = username;
            final String finalRole = role;
            final Long finalUserId = user.getId();
            
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return finalUsername;
                        }
                    };
                }
                
                @Override
                public boolean isUserInRole(String r) {
                    return finalRole.equals(r);
                }
                
                @Override
                public boolean isSecure() {
                    return requestContext.getUriInfo().getBaseUri().getScheme().equals("https");
                }
                
                @Override
                public String getAuthenticationScheme() {
                    return AUTHENTICATION_SCHEME;
                }
            });
            
            // Add user ID to request properties for easy access
            requestContext.setProperty("userId", finalUserId);
            LOGGER.info("Successfully authenticated request for endpoint: " + path);
            
        } catch (Exception e) {
            LOGGER.severe("Error processing JWT token for endpoint " + path + ": " + e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }
    
    private boolean isPublicEndpoint(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        LOGGER.info("Checking if endpoint is public: " + path);
        
        // Remove leading slash if present
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        // Check for public endpoints
        boolean isPublic = path.equals("users/login") || 
                          path.equals("users/register") ||
                          path.equals("api/users/login") ||
                          path.equals("api/users/register");
        
        LOGGER.info("Endpoint " + path + " is " + (isPublic ? "public" : "protected"));
        return isPublic;
    }
    
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                .entity(message)
                .build()
        );
    }
} 