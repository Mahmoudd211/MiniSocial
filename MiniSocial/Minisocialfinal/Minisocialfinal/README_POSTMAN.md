# MiniSocial API Testing Guide

This folder contains Postman collection and environment files for testing the MiniSocial API. The collection covers all major endpoints including authentication, user management, posts, notifications, friends, and the event-driven architecture.

## Setup Instructions

1. **Install Postman**: Download and install from [postman.com](https://www.postman.com/downloads/)

2. **Import the Collection**:
   - Open Postman
   - Click "Import" in the top left
   - Select the `postman_collection.json` file

3. **Import the Environment**:
   - In Postman, click "Import" again
   - Select the `postman_environment.json` file
   - Select the "MiniSocial Local" environment from the environment dropdown in the top right

## Key Features to Test

### Authentication
- Use the X-User-ID header for authentication (simplified for testing)
- Default value is 1, but you can change it to test different user contexts

### Notifications
- Get current user notifications
- Get notifications by user ID
- Mark notifications as read
- Test both read and unread filtering

### Event System
- Trigger different notification types via the Events API
- Test complete flow from event generation to notification delivery
- Events include: post creation, likes, comments, and friend requests

## Common Issues and Solutions

If notification endpoints aren't working:

1. **Authentication Issues**:
   - Verify the X-User-ID header is set correctly
   - Check that the user exists in the database

2. **Event System Issues**:
   - Ensure JMS is properly configured in WildFly
   - Verify the NotificationEventConsumer is correctly processing events

3. **Transaction Issues**:
   - Check for any transaction-related errors in server logs
   - Ensure proper transaction handling in the services

## Environment Variables

Key variable meanings:
- baseUrl: The base URL for API calls (default: http://localhost:8080)
- userId: ID to use for user-related operations
- postId: ID to use for post-related operations
- notificationId: ID for notification operations
- friendId: ID for friend operations
- requestId: ID for friend request operations

You can update these variables as needed for your specific testing scenario. 