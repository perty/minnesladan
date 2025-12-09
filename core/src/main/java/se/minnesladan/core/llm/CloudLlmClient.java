package se.minnesladan.core.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.EnumMap;
import java.util.Map;

@Component
public class CloudLlmClient implements LlmClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI apiUrl;
    private final String apiKey;
    private final Map<ModelTier, String> modelPerTier;

    public CloudLlmClient(
            @Value("${minnesladan.llm.cloud.api-url}") URI apiUrl,
            @Value("${minnesladan.llm.cloud.api-key}") String apiKey,
            @Value("${minnesladan.llm.cloud.model.high-capability}") String highCapabilityModel,
            @Value("${minnesladan.llm.cloud.model.low-cost}") String lowCostModel
    ) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        this.modelPerTier = new EnumMap<>(ModelTier.class);
        this.modelPerTier.put(ModelTier.HIGH_CAPABILITY, highCapabilityModel);
        this.modelPerTier.put(ModelTier.LOW_COST, lowCostModel);
        // ON_PREM hanteras inte här
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        String modelName = modelPerTier.get(request.modelTier());
        if (modelName == null) {
            throw new IllegalArgumentException(
                    "CloudLlmClient kan inte hantera modelTier=" + request.modelTier()
            );
        }

        try {
            String body = buildRequestBody(modelName, request);
            HttpRequest httpRequest = HttpRequest.newBuilder(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("LLM API error: " + response.statusCode() +
                                           " body=" + response.body());
            }

            return parseResponse(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fel vid anrop till LLM API", e);
        }
    }

    private String buildRequestBody(String modelName, LlmRequest request) throws IOException {
        // Detta är medvetet generiskt — anpassas till vald leverantör.
        var root = objectMapper.createObjectNode();
        root.put("model", modelName);
        root.put("max_tokens", request.maxTokens());
        root.put("temperature", request.temperature());

        var messagesNode = root.putArray("messages");
        for (var msg : request.messages()) {
            var m = messagesNode.addObject();
            m.put("role", msg.role().name().toLowerCase());
            m.put("content", msg.content());
        }

        return objectMapper.writeValueAsString(root);
    }

    private LlmResponse parseResponse(String body) throws IOException {
        // Även detta är generellt – anpassa till faktisk JSON-struktur senare.
        JsonNode root = objectMapper.readTree(body);

        // exempel: vi letar efter första "content"
        String content = root.at("/choices/0/message/content").asText("");

        long promptTokens = root.at("/usage/prompt_tokens").asLong(0);
        long completionTokens = root.at("/usage/completion_tokens").asLong(0);

        return new LlmResponse(content, promptTokens, completionTokens);
    }
}
