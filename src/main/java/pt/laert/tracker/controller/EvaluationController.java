package pt.laert.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.laert.tracker.model.dto.EvaluationRequest;
import pt.laert.tracker.model.dto.EvaluationResponse;
import pt.laert.tracker.service.EvaluationService;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public ResponseEntity<EvaluationResponse> evaluate(
            @RequestBody EvaluationRequest request
    ) {
        return ResponseEntity.ok(evaluationService.evaluateAssets(request));
    }
}
