package se.minnesladan.core.service;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class StoryImportService {

    private final ParagraphRepository paragraphRepository;

    public StoryImportService(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    @Transactional
    public int importFakeLifeStory() {
        String sectionName = "FakeLife";
        String path = "story/fake_life_story.txt";

        String text = readClasspathFile(path);
        List<String> paragraphs = splitIntoParagraphs(text);

        // Rensa tidigare stycken för den här sektionen
        paragraphRepository.deleteBySection(sectionName);

        int position = 1;
        List<Paragraph> entities = new ArrayList<>();
        for (String p : paragraphs) {
            Paragraph para = new Paragraph(
                    UUID.randomUUID(),
                    sectionName,
                    position++,
                    p
            );
            entities.add(para);
        }

        paragraphRepository.saveAll(entities);
        return entities.size();
    }

    private String readClasspathFile(String path) {
        Resource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte läsa filen från classpath: " + path, e);
        }
    }

    private List<String> splitIntoParagraphs(String text) {
        // Dela på tomrad(er) – funkar för “enkel” text
        String[] raw = text.split("\\R\\s*\\R");
        List<String> result = new ArrayList<>();
        for (String part : raw) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}

