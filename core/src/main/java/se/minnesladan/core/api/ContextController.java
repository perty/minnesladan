package se.minnesladan.core.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.service.ContextRetrievalService;

import java.util.List;

@RestController
@RequestMapping("/api/context")
public class ContextController {

    private final ContextRetrievalService retrievalService;

    public ContextController(ContextRetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }
   
    @PostMapping("/query")
    public RetrievalResponse query(@RequestBody RetrievalRequest request) {
        List<Paragraph> result = retrievalService.findRelevantParagraphs(
                request.question(),
                5 // just nu returnerar vi max 5 stycken
        );

        return new RetrievalResponse(request.question(), result);
    }
}
