package com.twa.flights.api.provider.alpha.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CompressionHelper {

    public static byte[] compress(String value) throws IOException {
        if (value == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(value.length())) {
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(value.getBytes());
            gzip.close();
            return baos.toByteArray();
        }
    }

    public static String decompress(byte[] compressedValue) throws IOException {
        if (compressedValue == null) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedValue);
                GZIPInputStream gzip = new GZIPInputStream(bais)) {
            StringBuilder sb = new StringBuilder();
            int b;
            while ((b = gzip.read()) != -1) {
                sb.append((char) b);
            }
            return sb.toString();
        }
    }

    private CompressionHelper() {
    }
}
