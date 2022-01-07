package com.twa.flights.api.clusters.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twa.flights.api.clusters.helper.CompressionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonSerializer.class);

    private final ObjectMapper objectMapper;

    public JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] serialize(Object object) {
        try {
            return CompressionHelper.compress(objectMapper.writeValueAsString(object));
        } catch (IOException e) {
            LOGGER.error("Error serializing object: {}", e.getMessage());
        }

        return null;
    }

    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try {
            return objectMapper.readValue(CompressionHelper.decompress(bytes), type);
        } catch (IOException e) {
            LOGGER.error("Error deserializing object: {}", e.getMessage());
        }

        return null;
    }
}
