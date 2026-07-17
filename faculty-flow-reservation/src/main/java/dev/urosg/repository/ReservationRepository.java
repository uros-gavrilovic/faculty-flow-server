package dev.urosg.repository;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>,
                                               JpaSpecificationExecutor<Reservation> {
	int countByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
	Optional<ReservationEntity> findByUuid(UUID uuid);
}
