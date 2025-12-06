package se.minnesladan.core.api;
import org.springframework.web.bind.annotation.*;

import se.minnesladan.core.service.ParagraphEmbeddingUpdateService;

@RestController
@RequestMapping("/api/admin/embeddings")
public class EmbeddingAdminController {

    private final ParagraphEmbeddingUpdateService updateService;

    public EmbeddingAdminController(ParagraphEmbeddingUpdateService updateService) {
        this.updateService = updateService;
    }

    @PostMapping("/update-missing")
    public Result updateMissing() {
        int updated = updateService.updateMissingEmbeddings();
        return new Result(updated);
    }

    public record Result(int updatedParagraphs) {}
}
