package se.minnesladan.core.service;

import se.minnesladan.core.database.Paragraph;

import java.util.List;

public interface ContextRetrievalService {
    List<Paragraph> findRelevantParagraphs(String question, int limit);
}
