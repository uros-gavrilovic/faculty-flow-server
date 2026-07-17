package dev.urosg.model.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationReviewedEvent(
	UUID uuid,
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reviewedBy,
	String status,
	String comment,
	String recipientEmail
) {}