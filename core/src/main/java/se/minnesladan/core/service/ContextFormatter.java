package se.minnesladan.core.service;

import org.springframework.stereotype.Service;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContextFormatter {

    private final ParagraphRepository paragraphRepository;

    public ContextFormatter(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    /**
     * Bygger upp ett textblock för LLM med rubrik + stycke.
     */
    public String formatWithHeadings(List<Paragraph> context) {
        return context.stream()
                .map(this::enrichWithHeading)
                .collect(Collectors.joining("\n\n"));
    }

    private String enrichWithHeading(Paragraph p) {
        // Om stycket själv är en rubrik – skicka bara texten som den är (eller hoppa över om du vill)
        if (p.isHeading()) {
            return p.getContent();
        }

        return paragraphRepository
                .findTopBySectionAndPositionLessThanAndIsHeadingTrueOrderByPositionDesc(
                        p.getSection(),
                        p.getPosition()
                )
                .map(heading -> "[Avsnitt: " + heading.getContent().trim() + "]\n" + p.getContent())
                .orElse(p.getContent());
    }
}
