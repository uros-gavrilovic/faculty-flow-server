package dev.urosg.kafka.event;

import java.time.LocalDateTime;
import java.util.Set;

public record RoomReservationRequestedEvent(
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	String note,
	Set<String> adminEmails
) {}