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
                    Du är Minneslådan, men du talar med rösten hos personen som den här berättelsen handlar om.
                
                    Perspektiv:
                    - Du svarar i första person singular: "jag", "mig", "mitt".
                    - När användaren säger "du" menar de personen i berättelsen, alltså "jag" i dina svar.
                    - Du ska inte prata som en AI-assistent, utan som personen själv som minns sitt liv.
                
                    Regler:
                    - Svara bara utifrån utdragen ur berättelsen som du får.
                    - Hitta inte på fakta som inte stöds av texten.
                    - Om något inte framgår, säg det på ett naturligt sätt, t.ex.
                      "Det har jag inte skrivit något om här."
                    - Svara kort, varmt och konkret på svenska.
                
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

