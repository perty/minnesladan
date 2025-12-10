package se.minnesladan.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Profile("openai-embeddings")
public class OpenAiEmbeddingService implements EmbeddingService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI apiUrl;
    private final String apiKey;
    private final String model;
    private final ParagraphRepository paragraphRepository;

    public OpenAiEmbeddingService(
            ParagraphRepository paragraphRepository,
            @Value("${minnesladan.llm.embedding.api-url:https://api.openai.com/v1/embeddings}")
            URI apiUrl,
            @Value("${minnesladan.llm.cloud.api-key}")
            String apiKey,
            @Value("${minnesladan.llm.embedding.model:text-embedding-3-small}")
            String model
    ) {
        this.paragraphRepository = paragraphRepository;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public float[] createEmbedding(String text) {
        try {
            String body = buildRequestBody(text);

            HttpRequest httpRequest = HttpRequest.newBuilder(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("Embedding API error: " + response.statusCode() +
                        " body=" + response.body());
            }

            return parseResponse(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fel vid anrop till Embedding API", e);
        }
    }

    @Override
    public List<Paragraph> findNearestByEmbedding(String embeddingLiteral, int limit) {
        return paragraphRepository.findNearestByEmbeddingOpenAi(embeddingLiteral, limit);
    }

    private String buildRequestBody(String text) throws JsonProcessingException {
        var root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("input", text);
        return objectMapper.writeValueAsString(root);
    }

    private float[] parseResponse(String body) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(body);
        JsonNode embeddingNode = root.at("/data/0/embedding");
        if (embeddingNode.isMissingNode() || !embeddingNode.isArray()) {
            throw new RuntimeException("Oväntat embedding-svar: " + body);
        }

        int dim = embeddingNode.size();
        float[] v = new float[dim];
        for (int i = 0; i < dim; i++) {
            v[i] = (float) embeddingNode.get(i).asDouble();
        }
        return v;
    }
}
