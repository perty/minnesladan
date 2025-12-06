package se.minnesladan.core.api;

import java.util.List;

import se.minnesladan.core.database.Paragraph;

public record QueryResponse(String question, List<Paragraph> paragraphs) {

}
