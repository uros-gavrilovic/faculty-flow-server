package dev.urosg.model.dto;

import dev.urosg.model.enumeration.EventType;
import dev.urosg.model.enumeration.ReservationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record Reservation(
	UUID uuid,
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	String reviewedBy,
	EventType eventType,
	ReservationStatus status,
	String note,
	String comment
) {
}
