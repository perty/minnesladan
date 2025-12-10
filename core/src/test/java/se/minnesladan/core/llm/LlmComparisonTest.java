package se.minnesladan.core.llm;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.service.ContextRetrievalService;
import se.minnesladan.core.service.LlmAnsweringService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled("Run manually. Talks to LLM online and costs money.")
@ActiveProfiles("real-embeddings")
class LlmComparisonTest {

    @Autowired
    private LlmAnsweringService answeringService;

    @Autowired
    private ContextRetrievalService contextRetrievalService;

    @Test
    void compareHighCapabilityAndLowCost() {
        String question = "Vad jobbade din mamma med?";

        List<Paragraph> context = contextRetrievalService.findRelevantParagraphs(question, 10);
        assertFalse(context.isEmpty());

        String answerHigh = answeringService.answerWithTier(question, context, ModelTier.HIGH_CAPABILITY);
        System.out.println("HIGH_CAPABILITY: " + answerHigh);
        String answerLow = answeringService.answerWithTier(question, context, ModelTier.LOW_COST);
        System.out.println("LOW_COST      : " + answerLow);
        String answerOnPrem = answeringService.answerWithTier(question, context, ModelTier.ON_PREM);
        System.out.println("ON_PREM      : " + answerOnPrem);

        // 4. Enkla assertions: båda ska få med nyckelfaktan "sjuksköterska"
        assertTrue(answerHigh.toLowerCase().contains("sjuksköterska"),
                "Dyra modellen ska nämna 'sjuksköterska'");
        assertTrue(answerLow.toLowerCase().contains("sjuksköterska"),
                "Billiga modellen ska nämna 'sjuksköterska'");
        assertTrue(answerOnPrem.toLowerCase().contains("sjuksköterska"),
                "Onprem modellen ska nämna 'sjuksköterska'");
    }

}
