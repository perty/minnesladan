package se.minnesladan.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphSearchRepository;

@Service
public class ContextRetrievalService {
    private final ParagraphSearchRepository searchRepository;
    private final EmbeddingService embeddingService;

    public ContextRetrievalService(ParagraphSearchRepository searchRepository,
                                   EmbeddingService embeddingService) {
        this.searchRepository = searchRepository;
        this.embeddingService = embeddingService;
    }

    public List<Paragraph> findRelevantParagraphs(String question, int limit) {
        float[] queryEmbedding = embeddingService.createEmbedding(question);
        return searchRepository.findNearest(queryEmbedding, limit);
    }
}
