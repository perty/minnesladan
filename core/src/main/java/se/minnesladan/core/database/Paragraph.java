package se.minnesladan.core.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "paragraph")
public class Paragraph {

    @Id
    private UUID id;

    private String section;

    private Integer position;

    @Column(nullable = false)
    private String content;

    private boolean isHeading;

    public Paragraph() {}

    public Paragraph(UUID id, String section, Integer position, String content, boolean isHeading) {
        this.id = id;
        this.section = section;
        this.position = position;
        this.content = content;
        this.isHeading = isHeading;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHeading() {
        return isHeading;
    }

    public void setHeading(boolean heading) {
        isHeading = heading;
    }
}
