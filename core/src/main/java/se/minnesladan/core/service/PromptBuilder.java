package se.minnesladan.core.service;

import org.springframework.stereotype.Component;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.llm.LlmMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    public List<LlmMessage> buildAnswerMessages(String question, List<Paragraph> context) {
        String contextText = context.stream()
                .map(p -> "- " + p.getContent())
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = """
                Du är Minneslådan. Du svarar på frågor om en persons liv utifrån deras livsberättelse.
                Regler:
                - Svara bara utifrån utdragen du får.
                - Om något inte framgår, säg att det inte går att se i berättelsen.
                - Svara kort, varmt och konkret på svenska.
                - Du pratar direkt till personen som berättelsen handlar om.
                """;

        String userPrompt = """
                Utdrag ur berättelsen:
                %s

                Fråga från personen:
                "%s"
                """.formatted(contextText, question);

        return List.of(
                new LlmMessage(LlmMessage.Role.SYSTEM, systemPrompt),
                new LlmMessage(LlmMessage.Role.USER, userPrompt)
        );
    }
}

