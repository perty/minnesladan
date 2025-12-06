-- Körs en gång i databasen:
CREATE EXTENSION IF NOT EXISTS vector;

-- Tabell med dina livs-texter styckevis
CREATE TABLE paragraph (
    id          UUID PRIMARY KEY,
    section     TEXT,          -- t.ex. "Barndom", "Första jobbet"
    position    INT,           -- ordning inom sektionen
    content     TEXT NOT NULL, -- själva stycket
    embedding   VECTOR(1536)   -- dimension beror på vilken embeddings-modell du väljer
);

-- Index för snabb närmaste-granne-sökning
CREATE INDEX paragraph_embedding_idx
    ON paragraph
    USING ivfflat (embedding vector_l2_ops)
    WITH (lists = 100);

