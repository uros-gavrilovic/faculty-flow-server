package dev.urosg.model.dto;

import dev.urosg.model.enumeration.ReservationStatus;
import lombok.Builder;
import lombok.With;
import java.time.LocalDateTime;

@Builder
@With
public record ReservationFilter(
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	String reviewedBy,
	ReservationStatus status
) {}
