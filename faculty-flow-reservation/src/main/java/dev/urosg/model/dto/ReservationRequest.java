package dev.urosg.model.dto;

import java.time.LocalDateTime;

public record ReservationRequest(
	String room,
	String name,
	String reservedBy,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String note
) { }
