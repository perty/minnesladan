package se.minnesladan.core.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class OnPremLlmClient implements LlmClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI apiUrl;
    private final String modelName;

    public OnPremLlmClient(
            @Value("${minnesladan.llm.onprem.api-url:http://localhost:11434/v1/chat/completions}")
            URI apiUrl,
            @Value("${minnesladan.llm.onprem.model:llama3.2}")
            String modelName
    ) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.modelName = modelName;
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        try {
            String body = buildRequestBody(modelName, request);

            HttpRequest httpRequest = HttpRequest.newBuilder(apiUrl)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("OnPrem LLM API error: " + response.statusCode() +
                        " body=" + response.body());
            }

            return parseResponse(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fel vid anrop till on-prem LLM API", e);
        }
    }

    private String buildRequestBody(String modelName, LlmRequest request) throws JsonProcessingException {
        var root = objectMapper.createObjectNode();
        root.put("model", modelName);

        // Ollama OpenAI-compat: max_tokens
        root.put("max_tokens", request.maxTokens());
        root.put("temperature", request.temperature());

        var messagesNode = root.putArray("messages");
        for (var msg : request.messages()) {
            var m = messagesNode.addObject();
            m.put("role", msg.role().name().toLowerCase()); // system/user/assistant
            m.put("content", msg.content());
        }

        return objectMapper.writeValueAsString(root);
    }

    private LlmResponse parseResponse(String body) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(body);

        String content = root.at("/choices/0/message/content").asText("");

        long promptTokens = root.at("/usage/prompt_tokens").asLong(0);
        long completionTokens = root.at("/usage/completion_tokens").asLong(0);

        return new LlmResponse(content, promptTokens, completionTokens);
    }
}

