ALTER TABLE paragraph
    DROP COLUMN IF EXISTS embedding_on_prem;

ALTER TABLE paragraph
    ADD COLUMN embedding_on_prem vector(768);
