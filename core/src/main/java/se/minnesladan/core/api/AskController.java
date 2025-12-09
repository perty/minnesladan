package se.minnesladan.core.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.minnesladan.core.service.AskService;

@RestController
@RequestMapping("/api")
public class AskController {

    private final AskService askService;

    public AskController(AskService askService) {
        this.askService = askService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@RequestBody AskRequest request) {
        var result = askService.ask(request.question(), request.contextLimit());
        return new AskResponse(
                result.question(),
                result.answer(),
                result.contextParagraphs()
        );
    }
}
