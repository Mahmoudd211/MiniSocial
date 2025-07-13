package com.minisocial.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.minisocial.util.JsonUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base event class that represents any event in the system.
 * Designed to be serialized as a JSON object for the message queue.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialEvent implements Serializable {
    private String id;
    private String eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private String source;
    private String version = "1.0";
    
    // Default constructor for JSON deserialization
    public SocialEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.data = new HashMap<>();
    }
    
    public SocialEvent(String eventType) {
        this();
        this.eventType = eventType;
        this.source = "minisocial-app";
    }
    
    public SocialEvent(String eventType, String source) {
        this(eventType);
        this.source = source;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public SocialEvent addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
    
    /**
     * Converts this event to a JSON string.
     *
     * @return JSON string representation of this event
     */
    public String toJson() {
        return JsonUtil.toJson(this);
    }
    
    /**
     * Creates a SocialEvent from a JSON string.
     *
     * @param json JSON string
     * @return SocialEvent object
     */
    public static SocialEvent fromJson(String json) {
        return JsonUtil.fromJson(json, SocialEvent.class);
    }
    
    @Override
    public String toString() {
        return "SocialEvent{" +
                "id='" + id + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", source='" + source + '\'' +
                ", version='" + version + '\'' +
                ", data=" + data +
                '}';
    }
} 