package se.minnesladan.core.llm;

public record LlmMessage(
        Role role,
        String content
) {
    public enum Role {
        SYSTEM,
        USER,
        ASSISTANT
    }
}
