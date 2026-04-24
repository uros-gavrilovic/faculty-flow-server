package dev.urosg.model.entity;

import dev.urosg.model.enumeration.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "reservation")
public class ReservationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ColumnDefault("gen_random_uuid()")
	private UUID uuid;

	LocalDateTime startTime;

	LocalDateTime endTime;

	String reservedBy;

	ReservationStatus status;
}
