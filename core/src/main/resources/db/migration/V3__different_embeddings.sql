alter table paragraph
    add column embedding_open_ai vector(1536),
    add column embedding_on_prem vector(1536);

alter table paragraph
    drop column embedding;