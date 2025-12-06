package se.minnesladan.core.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

@Service
public class ContextRetrievalService {
    private final ParagraphRepository paragraphRepository;
    // vi låter embedding-grejerna finnas kvar men inte användas än
    // private final ParagraphSearchRepository searchRepository;
    // private final EmbeddingService embeddingService;

    public ContextRetrievalService(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    public List<Paragraph> findRelevantParagraphs(String question, int limit) {
        // ta första ordet i frågan som enkelt sökterm
        String term = extractMainTerm(question);
        List<Paragraph> hits = paragraphRepository.searchByContentLike(term);
        return hits.stream().limit(limit).toList();
    }

    private String extractMainTerm(String question) {
        if (question == null) return "";
        // superenkelt: dela på mellanslag, ta ett “intressant” ord
        return Arrays.stream(question.split("\\s+"))
                     .filter(w -> w.length() > 3)
                     .findFirst()
                     .orElse(question);
    }
}
