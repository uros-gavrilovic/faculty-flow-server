package dev.urosg.kafka.event;

import java.time.LocalDateTime;

public record RoomReservationReviewedEvent(
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reviewedBy,
	String comment,
	String recipientEmail
) {}