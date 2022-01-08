package com.twa.flights.api.clusters.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompressionHelperTest {

    @Test
    public void should_compress_string_value_and_decompress_to_the_same_value() throws Exception {
        byte[] compressed = CompressionHelper.compress("This is a secret");
        String result = CompressionHelper.decompress(compressed);

        assertThat(result).isEqualTo("This is a secret");
    }
}
