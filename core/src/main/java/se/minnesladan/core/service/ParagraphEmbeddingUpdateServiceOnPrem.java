package se.minnesladan.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.minnesladan.core.database.EmbeddingColumn;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphSearchRepository;

import java.util.List;

@Service
@Profile("onprem-embeddings")
public class ParagraphEmbeddingUpdateServiceOnPrem implements ParagraphEmbeddingUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(ParagraphEmbeddingUpdateServiceOnPrem.class.getName());

    private final ParagraphSearchRepository searchRepository;
    private final EmbeddingService embeddingService;

    public ParagraphEmbeddingUpdateServiceOnPrem(ParagraphSearchRepository searchRepository,
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
        EmbeddingColumn column = EmbeddingColumn.ON_PREM;
        List<Paragraph> paragraphs = searchRepository.findAllWithoutEmbedding(column);
        for (Paragraph p : paragraphs) {
            logger.info("Updating embedding for paragraph {}", p.getPosition());
            float[] emb = embeddingService.createEmbedding(p.getContent());
            searchRepository.updateEmbedding(p.getId(), emb, column);
        }
        return paragraphs.size();
    }
}
