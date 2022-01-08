package com.twa.flights.api.clusters.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;

import com.twa.flights.api.clusters.dto.ClusterSearchDTO;

public class ClusterSearchSerializer implements RedisSerializer<ClusterSearchDTO> {

    private final JsonSerializer jsonSerializer;

    public ClusterSearchSerializer(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public byte[] serialize(ClusterSearchDTO clusterSearch) {
        return jsonSerializer.serialize(clusterSearch);
    }

    @Override
    public ClusterSearchDTO deserialize(byte[] bytes) {
        return jsonSerializer.deserialize(bytes, ClusterSearchDTO.class);
    }
}
