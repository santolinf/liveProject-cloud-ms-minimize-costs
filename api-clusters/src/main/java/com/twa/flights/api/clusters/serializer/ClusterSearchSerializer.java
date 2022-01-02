package com.twa.flights.api.clusters.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.twa.flights.api.clusters.dto.ClusterSearchDTO;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class ClusterSearchSerializer implements RedisSerializer<ClusterSearchDTO> {

    private final ObjectMapper objectMapper;

    public ClusterSearchSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(ClusterSearchDTO clusterSearch) {
        try {
            return objectMapper.writeValueAsBytes(clusterSearch);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    @Override
    public ClusterSearchDTO deserialize(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, ClusterSearchDTO.class);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
