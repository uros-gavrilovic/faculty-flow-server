package dev.urosg.model.event;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ReservationRequestedEvent(
	UUID uuid,
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	String note,
	Set<String> adminEmails
) {}