package dev.urosg.model.dto;

import dev.urosg.model.enumeration.ReservationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record Reservation(
	UUID nuuid,
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	ReservationStatus status,
	String note
) {
	Reservation(
		UUID nuuid,
		String name,
		String room,
		LocalDateTime startTime,
		LocalDateTime endTime,
		String reservedBy,
		ReservationStatus status
	) {
		this(nuuid, name, room, startTime, endTime, reservedBy, status, null);
	}
}
