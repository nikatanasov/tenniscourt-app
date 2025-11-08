package app.training_session.client;

import app.training_session.dto.TrainingSession;
import app.training_session.dto.TrainingSessionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "training-svc", url = "http://localhost:8082/api/v1/trainings")
public interface TrainingSessionClient {

    @PostMapping
    ResponseEntity<Void> bookSession(@RequestBody TrainingSessionRequest trainingSessionRequest);

    @PutMapping("/{id}")
    ResponseEntity<Void> cancelSession(@PathVariable UUID id);

    @GetMapping
    ResponseEntity<List<TrainingSession>> getTrainingSessionsByUserId(@RequestParam UUID userId);

    @GetMapping("/{id}")
    ResponseEntity<TrainingSession> getTrainingSessionById(@PathVariable UUID id);
}
