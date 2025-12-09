package se.minnesladan.core.service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

@Service
public class ContextRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(ContextRetrievalService.class);

    private final ParagraphRepository paragraphRepository;

    // Väldigt enkel lista med stoppord (kan utökas)
    private static final Set<String> STOPWORDS = Set.of(
            "vad", "hur", "varför", "när",
            "min", "mitt", "mina", "din", "ditt", "dina",
            "han", "hon", "den", "det", "de",
            "med", "som", "för", "att", "och", "eller", "utan", "också",
            "på", "i", "om", "till", "från"
    );

    private static final Set<String> FAMILY_WORDS = Set.of(
            "pappa", "mamma", "far", "mor", "bror", "syster", "son", "dotter", "barn"
    );

    public ContextRetrievalService(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    public List<Paragraph> findRelevantParagraphs(String question, int limit) {
        if (question == null || question.isBlank()) {
            return List.of();
        }

        // 1. Ta fram kandidat-termer ur frågan
        List<String> terms = extractSearchTerms(question);

        log.debug("ContextRetrieval: question=\"{}\" → searchTerms={}", question, terms);

        // 2. Sök på flera termer tills vi fått ihop 'limit' stycken
        LinkedHashSet<Paragraph> result = new LinkedHashSet<>();

        for (String term : terms) {
            List<Paragraph> hits = paragraphRepository.searchByContentLike(term);

            for (Paragraph p : hits) {
                result.add(p);
                if (result.size() >= limit) {
                    break; // stoppa inre loop
                }
            }
        }

        if (result.isEmpty()) {
            log.info("ContextRetrieval: inga träffar för question=\"{}\" (terms={})", question, terms);
            return List.of();
        }

        return result.stream()
                .limit(limit)
                .toList();
    }

    /**
     * Plockar ut "bättre" sökord ur frågan:
     * - normaliserar (små bokstäver, tar bort skiljetecken)
     * - tar bort stoppord
     * - prioriterar familjeord
     */
    private List<String> extractSearchTerms(String question) {
        List<String> tokens = Arrays.stream(question.split("\\s+"))
                .map(this::normalizeWord)
                .filter(w -> w.length() > 2)
                .filter(w -> !STOPWORDS.contains(w))
                .toList();

        // Sortera så att familjeord kommer först
        return tokens.stream()
                .sorted((a, b) -> {
                    boolean aFam = FAMILY_WORDS.contains(a);
                    boolean bFam = FAMILY_WORDS.contains(b);
                    if (aFam == bFam) return 0;
                    return aFam ? -1 : 1; // familjeord först
                })
                .toList();
    }

    private String normalizeWord(String w) {
        if (w == null) return "";
        // Ta bort skiljetecken, gör till lowercase
        return w.toLowerCase().replaceAll("[^\\p{L}\\p{Nd}]", "");
    }
}
