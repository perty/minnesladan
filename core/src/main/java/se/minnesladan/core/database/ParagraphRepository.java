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
}

