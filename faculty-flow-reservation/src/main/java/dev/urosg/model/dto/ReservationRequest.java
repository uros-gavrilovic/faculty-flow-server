package dev.urosg.model.dto;

import dev.urosg.model.enumeration.EventType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationRequest(
	String name,
	String roomCode,
	LocalDateTime startTime,
	LocalDateTime endTime,
	EventType eventType,
	String reservedBy,
	String note
) { }
