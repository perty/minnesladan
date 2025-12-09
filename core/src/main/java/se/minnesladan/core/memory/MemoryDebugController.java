package se.minnesladan.core.memory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryDebugController {

    private final MemorySummaryService memorySummaryService;

    public MemoryDebugController(MemorySummaryService memorySummaryService) {
        this.memorySummaryService = memorySummaryService;
    }

    @GetMapping("/api/debug/summary")
    public String testSummary(@RequestParam("text") String text) {
        return memorySummaryService.summarizeEntry(text);
    }
}
