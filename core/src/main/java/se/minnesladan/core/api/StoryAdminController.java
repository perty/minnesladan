package se.minnesladan.core.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.minnesladan.core.service.StoryImportService;

@RestController
@RequestMapping("/api/admin/story")
public class StoryAdminController {

    private final StoryImportService storyImportService;

    public StoryAdminController(StoryImportService storyImportService) {
        this.storyImportService = storyImportService;
    }

    @PostMapping("/import-fake-life")
    public ImportResult importFakeLife() {
        int count = storyImportService.importFakeLifeStory();
        return new ImportResult("FakeLife", count);
    }

    public record ImportResult(String section, int paragraphsImported) {}
}
