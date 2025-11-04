package dev.locker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Singleton ObjectMapper configuration for the project.
 */
public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtil() {
    }

    /**
     * Return the shared Jackson {@link ObjectMapper} configured for this application.
     *
     * @return a singleton ObjectMapper instance (thread-safe for typical usage)
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * Produce a pretty-printed JSON representation of the given object.
     *
     * @param obj the object to serialize to JSON
     * @return the pretty JSON string
     * @throws com.fasterxml.jackson.core.JsonProcessingException if serialization fails
     */
    public static String pretty(Object obj) throws com.fasterxml.jackson.core.JsonProcessingException {
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
