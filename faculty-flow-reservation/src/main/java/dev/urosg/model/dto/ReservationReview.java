package dev.urosg.model.dto;

import java.util.UUID;
import dev.urosg.model.enumeration.ReservationStatus;

public record ReservationReview(
	UUID uuid,
	ReservationStatus status
) {
}
