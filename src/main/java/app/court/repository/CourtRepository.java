package app.court.repository;

import app.court.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourtRepository extends JpaRepository<Court, UUID> {

    Optional<Court> findByName(String name);
}
