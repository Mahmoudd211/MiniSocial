# MiniSocial Event System Documentation

This document provides instructions for setting up and testing the event-driven notification system in the MiniSocial application.

## Overview

The event system uses JMS (Java Message Service) with a standardized JSON-based event format to decouple notification generation from processing. This architecture improves scalability and maintainability while providing a clear separation of concerns.

## Setup Instructions

### 1. Configure JMS Queue in WildFly

Before deploying the application, you need to set up the JMS queue in your WildFly server:

1. Start your WildFly server
2. Run the provided CLI script to create the notification queue:

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect --file=setup-jms-queue.cli
```

### 2. Add Required Dependencies

Ensure your project includes the necessary dependencies in pom.xml:

- Jackson for JSON processing
- Jakarta EE libraries for CDI and JMS
- ActiveMQ/Artemis JMS client

### 3. Deploy the Application

Deploy the application to your WildFly server:

```bash
mvn clean package
cp target/minisocial.war $WILDFLY_HOME/standalone/deployments/
```

## Testing the Event System

### Method 1: Using the Test Endpoints

The application includes test endpoints specifically for verifying the event system:

1. **Single Test Event**: `GET /api/test/event`
   - Sends a simple test event through the system

2. **Comprehensive Test**: `GET /api/test/event/full`
   - Sends multiple event types to test all aspects of the system

### Method 2: Using the HTML Test Page

For a more user-friendly approach, open the provided `event-system-test.html` file in your browser:

1. Open the HTML file in your web browser
2. Verify the API base URL is correct (typically `http://localhost:8080/minisocial/api`)
3. Click the test buttons to send events
4. View the results directly on the page

### Method 3: Using Postman

Import the provided Postman collection (`MiniSocial_API_Tests.postman_collection.json`):

1. Open Postman
2. Import the collection file
3. Navigate to the "Event System Tests" folder
4. Run the test requests
5. Check the server logs to verify events are being processed

## Verifying the System

To verify the system is working correctly:

1. Watch the server logs for messages from:
   - `NotificationEventService` - Should show "Published event as JSON: {...}"
   - `NotificationEventConsumer` - Should show "Received message" and "Processing event"

2. Check the database for new notifications:
   - New rows should appear in the NOTIFICATION table when events are processed

## Troubleshooting

If events are not being processed:

1. Check that the JMS queue was created successfully:
   ```bash
   $WILDFLY_HOME/bin/jboss-cli.sh --connect --command="/subsystem=messaging-activemq/server=default/jms-queue=NotificationQueue:read-resource"
   ```

2. Verify that resource injection is working by checking for errors in the server logs

3. Check the JNDI names in annotations and configuration match:
   - ConnectionFactory: `java:/ConnectionFactory`
   - Queue: `java:/jms/queue/NotificationQueue`

4. Ensure Jackson dependencies are correctly included in your WAR file

## Event Format

All events use the following standardized JSON format:

```json
{
  "id": "unique-uuid",
  "eventType": "EVENT_TYPE_NAME",
  "timestamp": "2023-07-01T14:30:00",
  "source": "minisocial-app",
  "version": "1.0",
  "data": {
    "recipientId": 123,
    "senderId": 456,
    "message": "Event message",
    "entityType": "TYPE",
    "entityId": 789
  }
}
```

## Adding New Event Types

To add new event types:

1. Add the new type to the `EventType` enum
2. Create a method in your service to create and publish the event
3. Update the consumer to handle the new event type appropriately 