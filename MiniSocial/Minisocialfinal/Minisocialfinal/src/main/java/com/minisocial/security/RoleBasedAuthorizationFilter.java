package com.minisocial.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleBasedAuthorizationFilter implements ContainerRequestFilter {
    
    private static final Logger LOGGER = Logger.getLogger(RoleBasedAuthorizationFilter.class.getName());
    
    @Context
    private ResourceInfo resourceInfo;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the required role from the annotation
        RequiresRole requiresRole = resourceInfo.getResourceMethod().getAnnotation(RequiresRole.class);
        if (requiresRole == null) {
            requiresRole = resourceInfo.getResourceClass().getAnnotation(RequiresRole.class);
        }
        
        if (requiresRole != null) {
            String requiredRole = requiresRole.value();
            SecurityContext securityContext = requestContext.getSecurityContext();
            
            if (securityContext == null || !securityContext.isUserInRole(requiredRole)) {
                LOGGER.warning("User does not have required role: " + requiredRole);
                requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                        .entity("Access denied. Required role: " + requiredRole)
                        .build()
                );
            }
        }
    }
} 