package com.twa.flights.api.clusters.serializer;

import com.twa.flights.api.clusters.helper.CompressionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class StringSerializer implements RedisSerializer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringSerializer.class);

    @Override
    public byte[] serialize(String value) throws SerializationException {
        try {
            return CompressionHelper.compress(value);
        } catch (IOException e) {
            LOGGER.error("Error serializing string: {}", e.getMessage());
            throw new SerializationException(e.getMessage());
        }
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        try {
            return CompressionHelper.decompress(bytes);
        } catch (IOException e) {
            LOGGER.error("Error deserializing string: {}", e.getMessage());
            throw new SerializationException(e.getMessage());
        }
    }
}
