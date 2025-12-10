alter table paragraph drop column embedding_on_prem;

alter table paragraph
    add column embedding_on_prem vector(3072);
