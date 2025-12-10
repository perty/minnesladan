package se.minnesladan.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class StoryImportService {

    private static final Logger logger = LoggerFactory.getLogger(StoryImportService.class);

    private static final String SECTION_NAME = "FakeLife";

    private final ParagraphRepository paragraphRepository;

    public StoryImportService(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    @Transactional
    public int importFakeLifeStory() {

        Resource[] resources = getResources();

        paragraphRepository.deleteBySection(SECTION_NAME);

        List<Paragraph> entities = getParagraphEntities(resources);

        paragraphRepository.saveAll(entities);

        return entities.size();
    }

    private List<Paragraph> getParagraphEntities(Resource[] resources) {
        int position = 1;
        List<Paragraph> entities = new ArrayList<>();

        for (Resource resource : resources) {
            for (String content : splitIntoParagraphs(readResource(resource))) {
                Paragraph para = new Paragraph(
                        UUID.randomUUID(),
                        SECTION_NAME,
                        position++,
                        content,
                        detectHeading(content)
                );
                entities.add(para);
            }
        }
        return entities;
    }

    private Resource[] getResources() {
        try {
            final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final Resource[] resources = resolver.getResources("classpath*:story/kapitel*.txt");
            if (resources.length == 0) {
                throw new IllegalStateException("Inga filer hittades för mönstret story/kapitel*.txt");
            }
            // Sortera efter filnamn, t.ex. kapitel_01_..., kapitel_02_...
            Arrays.sort(resources, Comparator.comparing(r -> {
                String name = r.getFilename();
                return name != null ? name : "";
            }));
            return resources;
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte lista kapitel-filerna", e);
        }
    }

    private List<String> splitIntoParagraphs(String text) {
        // Dela på tomrad(er) – funkar för “enkel” text
        final String[] raw = text.split("\\R\\s*\\R");
        final List<String> result = new ArrayList<>();
        for (String part : raw) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private boolean detectHeading(String p) {
        final String line = p.trim();

        // 1. Matchar "1. Någonting"
        if (line.matches("^\\d+\\..*")) return true;

        // 2. Börjar med Testdata – eller liknande
        if (line.startsWith("KAPITEL")) return true;

        // 3. Kort rad utan punkt
        return line.length() < 60 && !line.contains(".");
    }

    private String readResource(Resource resource) {
        final String filename = resource.getFilename();
        logger.info("Reading file {}", filename);
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte läsa filen: " + filename, e);
        }
    }
}

