package se.minnesladan.core.llm;

public record LlmResponse(
        String content,
        long promptTokens,
        long completionTokens
) {
}
