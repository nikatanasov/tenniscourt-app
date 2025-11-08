package app.training_session.service;

import app.training_session.client.TrainingSessionClient;
import app.training_session.dto.TrainingSession;
import app.training_session.dto.TrainingSessionRequest;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TrainingSessionService {
    private final TrainingSessionClient trainingSessionClient;

    @Autowired
    public TrainingSessionService(TrainingSessionClient trainingSessionClient) {
        this.trainingSessionClient = trainingSessionClient;
    }

    public List<TrainingSession> getAllTrainingsForUser(UUID userId) {
        ResponseEntity<List<TrainingSession>> httpResponse = trainingSessionClient.getTrainingSessionsByUserId(userId);
        return httpResponse.getBody();
    }

    public void cancelSession(UUID id){
        ResponseEntity<Void> httpResponse = trainingSessionClient.cancelSession(id);
    }

    public void bookSession(UUID userId, String courtName, LocalDateTime startTime, LocalDateTime endTime){
        TrainingSessionRequest trainingSessionRequest = TrainingSessionRequest.builder()
                .userId(userId)
                .courtName(courtName)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        ResponseEntity<Void> httpResponse = trainingSessionClient.bookSession(trainingSessionRequest);
        if(!httpResponse.getStatusCode().is2xxSuccessful()){
            log.error("Can't book session for user with id "+userId+"!");
        }
    }

    public TrainingSession getTrainingSessionById(UUID trainingId) {
        ResponseEntity<TrainingSession> httpResponse = trainingSessionClient.getTrainingSessionById(trainingId);

        if(!httpResponse.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Training session with id "+trainingId+" does not exist!");
        }
        return httpResponse.getBody();
    }
}
