package se.minnesladan.core.service;


import java.util.List;

import se.minnesladan.core.database.Paragraph;

public interface AnsweringService {

    /**
     * Skapa ett svar på frågan baserat på givna stycken.
     * I framtiden kopplas detta till en riktig LLM.
     */
    String answer(String question, List<Paragraph> context);
}

