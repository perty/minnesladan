package se.minnesladan.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;

@Service
public class AskService {

    private final ContextRetrievalService contextRetrievalService;
    private final AnsweringService answeringService;

    public AskService(ContextRetrievalService contextRetrievalService,
                      AnsweringService answeringService) {
        this.contextRetrievalService = contextRetrievalService;
        this.answeringService = answeringService;
    }

    public AskResult ask(String question, Integer contextLimit) {
        int limit = (contextLimit == null || contextLimit <= 0) ? 5 : contextLimit;

        List<Paragraph> context = contextRetrievalService.findRelevantParagraphs(question, limit);
        String answer = answeringService.answer(question, context);

        return new AskResult(question, answer, context);
    }

    public record AskResult(String question, String answer, List<Paragraph> contextParagraphs) {}
}
