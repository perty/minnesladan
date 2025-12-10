package se.minnesladan.core.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ParagraphSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public ParagraphSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Uppdatera embedding för ett visst stycke.
     */
    public void updateEmbedding(UUID paragraphId, float[] embedding) {
        String sql = """
            UPDATE paragraph
            SET embedding = ?::vector
            WHERE id = ?
            """;

        String vectorLiteral = toPgVectorLiteral(embedding);
        jdbcTemplate.update(sql, vectorLiteral, paragraphId);
    }

    /**
     * Hämta alla id + content för stycken som saknar embedding.
     */
    public List<Paragraph> findAllWithoutEmbedding() {
        String sql = """
            SELECT id, section, position, content
            FROM paragraph
            WHERE embedding IS NULL
            ORDER BY section, position
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    private static String toPgVectorLiteral(float[] embedding) {
        // pgvector-format: '[1.0, 2.0, 3.0]'
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private Paragraph mapRow(ResultSet rs) throws SQLException {
        return new Paragraph(
                (UUID) rs.getObject("id"),
                rs.getString("section"),
                (Integer) rs.getObject("position"),
                rs.getString("content")
        );
    }
}
