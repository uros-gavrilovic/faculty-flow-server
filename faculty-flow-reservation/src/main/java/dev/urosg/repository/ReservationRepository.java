package dev.urosg.repository;

import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
	Set<ReservationEntity> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
	Set<ReservationEntity> findByStartTimeLessThanAndEndTimeGreaterThanAndStatus(LocalDateTime end, LocalDateTime start, ReservationStatus status);
	Set<ReservationEntity> findByStartTimeLessThanAndEndTimeGreaterThanAndRoom(LocalDateTime end, LocalDateTime start, String room);
	Optional<ReservationEntity> findByUuid(UUID uuid);
}
