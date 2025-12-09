package se.minnesladan.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.llm.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class LlmAnsweringService implements AnsweringService {

    private static final Logger log = LoggerFactory.getLogger(LlmAnsweringService.class);

    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;

    public LlmAnsweringService(LlmClient llmClient, PromptBuilder promptBuilder) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public String answer(String question, List<Paragraph> context) {

        long start = System.currentTimeMillis();

        // -------------------------
        // 1. Logga fråga + context
        // -------------------------
        log.info("LLM-ASK: question=\"{}\"", question);

        log.debug("LLM-ASK context paragraphs ({} st):\n{}",
                context.size(),
                context.stream()
                        .map(p -> " - [" + p.getId() + "] " + abbreviate(p.getContent(), 120))
                        .collect(Collectors.joining("\n"))
        );

        // -------------------------
        // 2. Bygg messages
        // -------------------------
        List<LlmMessage> messages = promptBuilder.buildAnswerMessages(question, context);

        // Logga prompten förkortad
        log.debug("LLM-ASK prompt messages:\n{}",
                messages.stream()
                        .map(m -> m.role() + ": " + abbreviate(m.content(), 200))
                        .collect(Collectors.joining("\n\n"))
        );

        // -------------------------
        // 3. Skapa request
        // -------------------------
        LlmRequest request = new LlmRequest(
                messages,
                512,
                0.2,
                ModelTier.HIGH_CAPABILITY
        );

        // -------------------------
        // 4. Kör LLM
        // -------------------------
        LlmResponse response = llmClient.complete(request);

        long duration = System.currentTimeMillis() - start;

        // -------------------------
        // 5. Logga resultat
        // -------------------------
        log.info("LLM-ASK done in {} ms  (promptTokens={}, completionTokens={})",
                duration, response.promptTokens(), response.completionTokens()
        );

        log.debug("LLM-ANSWER (shortened): {}", abbreviate(response.content(), 300));

        // -------------------------
        // 6. Returnera svaret
        // -------------------------
        return response.content().trim();
    }

    private String abbreviate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max) + "...";
    }
}
