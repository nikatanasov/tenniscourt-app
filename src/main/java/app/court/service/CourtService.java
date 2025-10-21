package app.court.service;

import app.court.model.Court;
import app.court.repository.CourtRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CourtService {

    private final CourtRepository courtRepository;

    @Autowired
    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public void addCourt(Court court) {
        courtRepository.save(court);
    }

    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }

    public Court findById(UUID id) {
        return courtRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no court with id "+id+"!"));
    }

    public Court getCourtByName(String courtName) {
        return courtRepository.findByName(courtName).orElseThrow(() -> new RuntimeException("There is no court with name "+courtName+"!"));
    }
}
