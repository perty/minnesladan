package se.minnesladan.core.service;

import se.minnesladan.core.database.Paragraph;

import java.util.List;

public interface EmbeddingService {
    float[] createEmbedding(String text);

    List<Paragraph> findNearestByEmbedding(String embeddingLiteral, int limit);
}
