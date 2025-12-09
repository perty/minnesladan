package se.minnesladan.core.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Placeholder för egen hostad modell.
 * Kan t.ex. prata med en lokal Ollama/vLLM-server.
 */
@Component
public class OnPremLlmClient implements LlmClient {

    private final URI onPremApiUrl;

    public OnPremLlmClient(
            @Value("${minnesladan.llm.onprem.api-url:http://localhost:11434/api/generate}")
            URI onPremApiUrl
    ) {
        this.onPremApiUrl = onPremApiUrl;
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        // Just nu en enkel stub – vi kan senare lägga in riktig HTTP-logik här.
        return new LlmResponse(
                "[ON-PREM STUB] " + request.messages().getLast().content(),
                0,
                0
        );
    }
}
