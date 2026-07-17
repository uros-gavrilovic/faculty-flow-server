package dev.urosg.model.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationRequest(
	String roomCode,
	String name,
	String reservedBy,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String note
) { }
