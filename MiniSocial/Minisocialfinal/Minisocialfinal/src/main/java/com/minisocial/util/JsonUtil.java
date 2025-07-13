package com.minisocial.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for JSON serialization and deserialization.
 */
public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    private static final ObjectMapper OBJECT_MAPPER;
    
    static {
        OBJECT_MAPPER = new ObjectMapper();
        // Register module to handle Java 8 date/time types
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
    /**
     * Converts an object to a JSON string.
     *
     * @param object The object to convert
     * @return JSON string representation of the object
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error converting object to JSON", e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
    
    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json  The JSON string to convert
     * @param clazz The class of the object to convert to
     * @param <T>   The type of the object
     * @return The deserialized object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error converting JSON to object", e);
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
} 