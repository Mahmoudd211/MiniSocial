package com.minisocial;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import com.minisocial.rest.UserResource;
import com.minisocial.rest.PostResource;
import com.minisocial.rest.FriendRequestResource;
import com.minisocial.rest.CommentResource;
import com.minisocial.rest.LikeResource;
import com.minisocial.rest.GroupResource;
import com.minisocial.rest.GroupPostResource;
import com.minisocial.rest.NotificationResource;
import com.minisocial.rest.TestResource;
import com.minisocial.ejb.JacksonConfig;
import com.minisocial.security.JwtAuthenticationFilter;
import com.minisocial.security.RoleBasedAuthorizationFilter;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import java.util.Set;
import java.util.HashSet;

@ApplicationPath("/api")
public class MiniSocialApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(UserResource.class);
        resources.add(PostResource.class);
        resources.add(FriendRequestResource.class);
        resources.add(CommentResource.class);
        resources.add(LikeResource.class);
        resources.add(GroupResource.class);
        resources.add(GroupPostResource.class);
        resources.add(NotificationResource.class);
        resources.add(TestResource.class);
        resources.add(JacksonConfig.class);
        resources.add(JwtAuthenticationFilter.class);
        resources.add(RoleBasedAuthorizationFilter.class);
        return resources;
    }
} 