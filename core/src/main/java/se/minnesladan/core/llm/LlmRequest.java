package se.minnesladan.core.llm;

import java.util.List;

public record LlmRequest(
         List<LlmMessage> messages,
         int maxTokens,
         double temperature,
         ModelTier modelTier
) {
}
