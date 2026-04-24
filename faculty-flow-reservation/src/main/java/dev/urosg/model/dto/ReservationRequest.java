package dev.urosg.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationRequest(
	UUID roomUuid,
	String name,
	String reservedBy,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String note
) { }
