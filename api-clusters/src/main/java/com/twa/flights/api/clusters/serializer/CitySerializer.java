package com.twa.flights.api.clusters.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twa.flights.api.clusters.dto.CityDTO;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class CitySerializer implements RedisSerializer<CityDTO> {

    private final ObjectMapper objectMapper;

    public CitySerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(CityDTO cityDTO) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(cityDTO);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    @Override
    public CityDTO deserialize(byte[] bytes) throws SerializationException {
        try {
            return objectMapper.readValue(bytes, CityDTO.class);
        } catch (IOException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
