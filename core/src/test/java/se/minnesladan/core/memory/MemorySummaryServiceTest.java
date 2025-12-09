package se.minnesladan.core.memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.minnesladan.core.llm.LlmClient;
import se.minnesladan.core.llm.LlmMessage;
import se.minnesladan.core.llm.LlmRequest;
import se.minnesladan.core.llm.LlmResponse;
import se.minnesladan.core.llm.ModelTier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemorySummaryServiceTest {

    @Mock
    private LlmClient llmClient;   // mock istället för riktig implementation

    @InjectMocks
    private MemorySummaryService memorySummaryService;

    @Captor
    private ArgumentCaptor<LlmRequest> requestCaptor;

    @Test
    void summarizeEntry_callsLlmWithHighCapabilityAndReturnsContent() {
        // given
        String rawText = "Idag gick jag en lång promenad i skogen och kände mig lugn.";
        String llmAnswer = "Du gick en lugn promenad i skogen idag.";

        // mocka vad LLM ska svara
        when(llmClient.complete(any(LlmRequest.class)))
                .thenReturn(new LlmResponse(llmAnswer, 42, 17));

        // when
        String summary = memorySummaryService.summarizeEntry(rawText);

        // then: kolla att vi får tillbaka den mockade texten
        assertThat(summary).isEqualTo(llmAnswer);

        // och kolla hur vi faktiskt kallade LLM
        verify(llmClient, times(1)).complete(requestCaptor.capture());
        LlmRequest sentRequest = requestCaptor.getValue();

        // 1) rätt modelTier
        assertThat(sentRequest.modelTier()).isEqualTo(ModelTier.HIGH_CAPABILITY);

        // 2) rimliga meddelanden
        List<LlmMessage> messages = sentRequest.messages();
        assertThat(messages).hasSize(2);

        LlmMessage systemMsg = messages.get(0);
        LlmMessage userMsg = messages.get(1);

        assertThat(systemMsg.role()).isEqualTo(LlmMessage.Role.SYSTEM);
        assertThat(systemMsg.content()).contains("Minneslådan")
                .contains("Sammanfatta");

        assertThat(userMsg.role()).isEqualTo(LlmMessage.Role.USER);
        assertThat(userMsg.content()).isEqualTo(rawText);

        // 3) lite sanity på maxTokens/temperature om du vill
        assertThat(sentRequest.maxTokens()).isGreaterThan(0);
        assertThat(sentRequest.temperature()).isBetween(0.0, 1.0);
    }
}
