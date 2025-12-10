package se.minnesladan.core.service;

import org.springframework.transaction.annotation.Transactional;

public interface ParagraphEmbeddingUpdateService {
    @Transactional
    int updateMissingEmbeddings();
}
