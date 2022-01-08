package com.twa.flights.api.provider.beta.serializer;

import com.twa.flights.api.provider.beta.dto.CityDTO;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class CatalogSerializer implements RedisSerializer<CityDTO> {

    private final JsonSerializer jsonSerializer;

    public CatalogSerializer(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public byte[] serialize(CityDTO cityDTO) throws SerializationException {
        return jsonSerializer.serialize(cityDTO);
    }

    @Override
    public CityDTO deserialize(byte[] bytes) throws SerializationException {
        return jsonSerializer.deserialize(bytes, CityDTO.class);
    }
}
