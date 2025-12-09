package se.minnesladan.core.memory;

import org.springframework.stereotype.Service;
import se.minnesladan.core.llm.*;

import java.util.List;

@Service
public class MemorySummaryService {

    private final LlmClient llmClient;

    public MemorySummaryService(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    public String summarizeEntry(String rawText) {
        var messages = List.of(
                new LlmMessage(LlmMessage.Role.SYSTEM,
                        "Du är Minneslådan. Sammanfatta texten kort på svenska, " +
                        "med fokus på känslor och viktiga händelser."),
                new LlmMessage(LlmMessage.Role.USER, rawText)
        );

        var request = new LlmRequest(
                messages,
                400,
                0.3,
                ModelTier.HIGH_CAPABILITY
        );

        LlmResponse response = llmClient.complete(request);
        return response.content();
    }
}
