package se.minnesladan.core.service;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.minnesladan.core.database.EmbeddingColumn;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphSearchRepository;

import java.util.List;

@Service
@Profile("openai-embeddings")
public class ParagraphEmbeddingUpdateServiceOpenAI implements ParagraphEmbeddingUpdateService {

    private final ParagraphSearchRepository searchRepository;
    private final EmbeddingService embeddingService;

    public ParagraphEmbeddingUpdateServiceOpenAI(ParagraphSearchRepository searchRepository,
                                                 EmbeddingService embeddingService) {
        this.searchRepository = searchRepository;
        this.embeddingService = embeddingService;
    }

    /**
     * Skapar/uppdaterar embedding för alla stycken som saknar embedding.
     * Returnerar antal uppdaterade rader.
     */
    @Transactional
    @Override
    public int updateMissingEmbeddings() {
        EmbeddingColumn column = EmbeddingColumn.OPEN_AI;
        List<Paragraph> paragraphs = searchRepository.findAllWithoutEmbedding(column);
        for (Paragraph p : paragraphs) {
            float[] emb = embeddingService.createEmbedding(p.getContent());
            searchRepository.updateEmbedding(p.getId(), emb, column);
        }
        return paragraphs.size();
    }
}
