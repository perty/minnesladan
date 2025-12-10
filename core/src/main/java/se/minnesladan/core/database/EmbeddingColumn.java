package se.minnesladan.core.database;

public enum EmbeddingColumn {
    OPEN_AI("embedding_open_ai"),
    ON_PREM("embedding_on_prem");

    private final String columnName;

    EmbeddingColumn(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return columnName;
    }
}
