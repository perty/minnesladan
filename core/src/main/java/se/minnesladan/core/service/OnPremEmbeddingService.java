package se.minnesladan.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Profile("onprem-embeddings")
public class OnPremEmbeddingService implements EmbeddingService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ParagraphRepository paragraphRepository;
    private final URI apiUrl;
    private final String model;

    public OnPremEmbeddingService(
            ParagraphRepository paragraphRepository,
            @Value("${minnesladan.embedding.onprem.api-url:http://localhost:11434/v1/embeddings}")
            URI apiUrl,
            @Value("${minnesladan.embedding.onprem.model:llama3.2}")
            String model
    ) {
        this.paragraphRepository = paragraphRepository;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    @Override
    public float[] createEmbedding(String text) {
        try {
            var root = objectMapper.createObjectNode();
            root.put("model", model);
            root.put("input", text);

            String body = objectMapper.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder(apiUrl)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("OnPrem embeddings fel: " +
                        response.statusCode() + " body=" + response.body());
            }

            JsonNode json = objectMapper.readTree(response.body());
            JsonNode embeddingNode = json.at("/data/0/embedding");
            int dim = embeddingNode.size();
            float[] v = new float[dim];
            for (int i = 0; i < dim; i++) {
                v[i] = (float) embeddingNode.get(i).asDouble();
            }
            return v;
        } catch (Exception e) {
            throw new RuntimeException("Fel vid on-prem embedding", e);
        }
    }

    @Override
    public List<Paragraph> findNearestByEmbedding(String embeddingLiteral, int limit) {
        return paragraphRepository.findNearestByEmbeddingOnPrem(embeddingLiteral, limit);
    }
}
