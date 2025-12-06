package se.minnesladan.core.service;

import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Enkel fake-implementation:
 * Mappar text → vektor på stabilt sätt.
 * Senare kan du ersätta den med en riktig modell.
 */
@Service
@Primary
public class FakeEmbeddingService implements EmbeddingService {

    private static final int DIM = 1536; // måste matcha din VECTOR-dimension i DB om du vill använda pgvector på riktigt

    @Override
    public float[] createEmbedding(String text) {
       float[] v = new float[DIM];
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < bytes.length; i++) {
            int idx = i % DIM;
            v[idx] += (bytes[i] & 0xFF);
        }

        // Normalisera manuellt (ingen Arrays.stream för float[])
        float sumSq = 0f;
        for (float f : v) {
            sumSq += f * f;
        }

        float norm = (float) Math.sqrt(sumSq);
        if (norm > 0f) {
            for (int i = 0; i < v.length; i++) {
                v[i] = v[i] / norm;
            }
        }

        return v;
    }
}
