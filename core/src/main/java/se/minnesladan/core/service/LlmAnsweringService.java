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
    private static final int MAX_TOKENS = 512;
    private static final double TEMPERATURE = 0.2;

    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;

    public LlmAnsweringService(LlmClient llmClient, PromptBuilder promptBuilder) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public String answer(String question, List<Paragraph> context) {
        return answerWithTier(question, context, ModelTier.HIGH_CAPABILITY);
    }

    public String answerWithTier(String question, List<Paragraph> context, ModelTier modelTier) {
        long start = System.currentTimeMillis();
        log.info("LLM-ASK: question=\"{}\"", question);

        debugLogContext(context);

        List<LlmMessage> messages = promptBuilder.buildAnswerMessages(question, context);

        debugLogPrompt(messages);

        LlmResponse response = getLlmResponse(messages, modelTier);

        long duration = System.currentTimeMillis() - start;

        log.info("LLM-ASK done in {} ms  (promptTokens={}, completionTokens={})",
                duration, response.promptTokens(), response.completionTokens()
        );

        debugLogResponse(response);

        return response.content().trim();
    }

    private LlmResponse getLlmResponse(List<LlmMessage> messages, ModelTier modelTier) {
        return llmClient.complete(new LlmRequest(
                messages,
                MAX_TOKENS,
                TEMPERATURE,
                modelTier
        ));
    }

    private void debugLogContext(List<Paragraph> context) {
        log.debug("LLM-ASK context paragraphs ({} st):\n{}",
                context.size(),
                context.stream()
                        .map(p -> " - [" + p.getId() + "] " + abbreviate(p.getContent(), 120))
                        .collect(Collectors.joining("\n"))
        );
    }

    private void debugLogPrompt(List<LlmMessage> messages) {
        log.debug("LLM-ASK prompt messages:\n{}",
                messages.stream()
                        .map(m -> m.role() + ": " + abbreviate(m.content(), 200))
                        .collect(Collectors.joining("\n\n"))
        );
    }

    private void debugLogResponse(LlmResponse response) {
        log.debug("LLM-ANSWER (shortened): {}", abbreviate(response.content(), 300));
    }

    private String abbreviate(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max) + "...";
    }
}
