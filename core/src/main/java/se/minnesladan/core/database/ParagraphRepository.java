package se.minnesladan.core.database;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ParagraphRepository extends JpaRepository<Paragraph, UUID> {

        void deleteBySection(String section);
}

