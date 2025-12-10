package se.minnesladan.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

import java.util.List;

@Service
@Primary
public class ContextRetrievalEmbeddingService implements ContextRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(ContextRetrievalEmbeddingService.class);

    private final ParagraphRepository paragraphRepository;
    private final EmbeddingService embeddingService;

    public ContextRetrievalEmbeddingService(ParagraphRepository paragraphRepository, EmbeddingService embeddingService) {
        this.paragraphRepository = paragraphRepository;
        this.embeddingService = embeddingService;
    }

    @Override
    public List<Paragraph> findRelevantParagraphs(String question, int limit) {
        if (question == null || question.isBlank()) {
            log.info("ContextRetrieval: tom fråga → tom kontext");
            return List.of();
        }

        // 1. Skapa embedding för frågan
        float[] embedding = embeddingService.createEmbedding(question);

        // 2. Gör om till pgvector-literal, t.ex. "[0.1,0.2,...]"
        String embeddingLiteral = toPgVectorLiteral(embedding);

        // 3. Fråga databasen
        List<Paragraph> hits =
                paragraphRepository.findNearestByEmbedding(embeddingLiteral, limit);

        log.info("ContextRetrieval: question=\"{}\" → {} träffar (limit={})",
                shorten(question, 80), hits.size(), limit);

        if (hits.isEmpty()) {
            log.info("ContextRetrieval: inga relevanta stycken hittades.");
            return List.of();
        }

        log.debug("ContextRetrieval: första träffen id={} snippet=\"{}\"",
                hits.getFirst().getId(),
                shorten(hits.getFirst().getContent(), 120));

        return hits;
    }

    private String toPgVectorLiteral(float[] v) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < v.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            // Viktigt: punkt som decimal, inte komma
            sb.append(v[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private String shorten(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }
}
