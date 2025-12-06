package se.minnesladan.core.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphSearchRepository;

import java.util.List;

@Service
public class ParagraphEmbeddingUpdateService {

    private final ParagraphSearchRepository searchRepository;
    private final EmbeddingService embeddingService;

    public ParagraphEmbeddingUpdateService(ParagraphSearchRepository searchRepository,
                                           EmbeddingService embeddingService) {
        this.searchRepository = searchRepository;
        this.embeddingService = embeddingService;
    }

    /**
     * Skapar/uppdaterar embedding för alla stycken som saknar embedding.
     * Returnerar antal uppdaterade rader.
     */
    @Transactional
    public int updateMissingEmbeddings() {
        List<Paragraph> paragraphs = searchRepository.findAllWithoutEmbedding();
        for (Paragraph p : paragraphs) {
            float[] emb = embeddingService.createEmbedding(p.getContent());
            searchRepository.updateEmbedding(p.getId(), emb);
        }
        return paragraphs.size();
    }
}
