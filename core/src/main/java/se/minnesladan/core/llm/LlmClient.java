package se.minnesladan.core.llm;

public interface LlmClient {

    LlmResponse complete(LlmRequest request);

}
