package se.minnesladan.core.api;

import se.minnesladan.core.database.Paragraph;

import java.util.List;

public record AskResponse(String question, String answer, List<Paragraph> contextParagraphs) {
}
