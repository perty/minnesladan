package se.minnesladan.core.service;


import java.util.List;

import se.minnesladan.core.database.Paragraph;

public interface AnsweringService {
    String answer(String question, List<Paragraph> context);
}

