package com.minisocial.rest;

import com.minisocial.test.EventSystemTester;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for testing purposes.
 */
@Path("/test")
public class TestResource {
    
    @Inject
    private EventSystemTester eventSystemTester;
    
    /**
     * Test endpoint to verify the event system is working correctly.
     */
    @GET
    @Path("/event")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testEvent() {
        try {
            eventSystemTester.sendTestEvent();
            return Response.ok("{\"status\":\"success\",\"message\":\"Test event sent successfully\"}").build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * Run a comprehensive test of the event system.
     */
    @GET
    @Path("/event/full")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testFullEventSystem() {
        try {
            eventSystemTester.runFullTest();
            return Response.ok("{\"status\":\"success\",\"message\":\"Full event system test completed successfully\"}").build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
} 