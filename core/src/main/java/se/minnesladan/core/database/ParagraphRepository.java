package se.minnesladan.core.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ParagraphRepository extends JpaRepository<Paragraph, UUID> {

    void deleteBySection(String section);

    @Query("""
            SELECT p
            FROM Paragraph p
            WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :term, '%'))
            ORDER BY p.section, p.position
            """)
    List<Paragraph> searchByContentLike(@Param("term") String term);

    @Query(value = """
            SELECT *
            FROM paragraph
            ORDER BY embedding_open_ai <-> CAST(:queryEmbedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Paragraph> findNearestByEmbeddingOpenAi(@Param("queryEmbedding") String queryEmbedding,
                                                 @Param("limit") int limit);

    @Query(value = """
            SELECT *
            FROM paragraph
            ORDER BY embedding_on_prem <-> CAST(:queryEmbedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Paragraph> findNearestByEmbeddingOnPrem(@Param("queryEmbedding") String queryEmbedding,
                                                 @Param("limit") int limit);
}

