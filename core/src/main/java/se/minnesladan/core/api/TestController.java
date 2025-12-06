package se.minnesladan.core.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.minnesladan.core.database.Paragraph;
import se.minnesladan.core.database.ParagraphRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ParagraphRepository repo;

    public TestController(ParagraphRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/paragraphs")
    public List<Paragraph> list() {
        return repo.findAll();
    }
}

