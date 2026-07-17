package dev.urosg.model.dto;

import dev.urosg.model.enumeration.EventType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationRequest(
	String roomCode,
	EventType eventType,
	String name,
	String reservedBy,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String note
) { }
