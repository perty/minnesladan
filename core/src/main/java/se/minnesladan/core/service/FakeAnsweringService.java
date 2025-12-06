package se.minnesladan.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import se.minnesladan.core.database.Paragraph;

@Service
@Primary
public class FakeAnsweringService implements AnsweringService {

    @Override
    public String answer(String question, List<Paragraph> context) {
        if (context == null || context.isEmpty()) {
            return "Jag hittar inga stycken i berättelsen som verkar höra ihop med den här frågan.";
        }

        // Superenkel “sammanfattning”: visa första raderna ur de första styckena
        String joined = context.stream()
                .map(Paragraph::getContent)
                .collect(Collectors.joining("\n\n"));

        if (joined.length() > 600) {
            joined = joined.substring(0, 600) + "...";
        }

        return """
               Jag kan inte ge ett riktigt intelligent svar ännu, \
               men här är några relevanta stycken ur berättelsen:

               %s
               """.formatted(joined);
    }
}
