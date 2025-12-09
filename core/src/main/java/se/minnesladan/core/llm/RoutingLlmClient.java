package se.minnesladan.core.llm;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RoutingLlmClient implements LlmClient {

    private final CloudLlmClient cloudLlmClient;
    private final OnPremLlmClient onPremLlmClient;

    public RoutingLlmClient(CloudLlmClient cloudLlmClient,
                            OnPremLlmClient onPremLlmClient) {
        this.cloudLlmClient = cloudLlmClient;
        this.onPremLlmClient = onPremLlmClient;
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        return switch (request.modelTier()) {
            case HIGH_CAPABILITY, LOW_COST -> cloudLlmClient.complete(request);
            case ON_PREM -> onPremLlmClient.complete(request);
        };
    }
}
