package dev.urosg.repository;

import dev.urosg.model.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
	Set<ReservationEntity> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
	Set<ReservationEntity> findByStartTimeLessThanAndEndTimeGreaterThanAndRoom(LocalDateTime end, LocalDateTime start, String room);
	Optional<ReservationEntity> findByUuid(UUID uuid);
}
