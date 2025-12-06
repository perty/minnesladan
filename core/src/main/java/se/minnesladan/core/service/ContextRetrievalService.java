package se.minnesladan.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

@Service
public class ContextRetrievalService {
    private final ParagraphRepository repo;

    public ContextRetrievalService(ParagraphRepository repo) {
        this.repo = repo;
    }

    public List<Paragraph> findRelevantParagraphs(String question, int limit) {
        // TODO: ersätt detta med embedding + pgvector
        return repo.findAll().stream().limit(limit).toList();
    }
}
