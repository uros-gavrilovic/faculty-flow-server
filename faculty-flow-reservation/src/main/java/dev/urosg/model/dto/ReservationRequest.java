package dev.urosg.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.urosg.model.enumeration.EventType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.Duration;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ReservationRequest(
	@NotBlank String name,
	@NotBlank String roomCode,
	@NotNull LocalDateTime startTime,
	@NotNull LocalDateTime endTime,
	@NotNull EventType eventType,
	String reservedBy, // Optional: defaults to the authenticated user
	String note
) {

	public static final Duration MAX_DURATION = Duration.ofHours(8);

	@JsonIgnore
	@AssertTrue(message = "End time must be after start time")
	public boolean isTimeRangeValid() {
		if (startTime == null || endTime == null) return true; // @NotNull
		return startTime.isBefore(endTime);
	}

	@JsonIgnore
	@AssertTrue(message = "Reservation cannot be longer than 8 hours")
	public boolean isDurationValid() {
		if (startTime == null || endTime == null) return true; // @NotNull
		return Duration.between(startTime, endTime).compareTo(MAX_DURATION) <= 0;
	}
}
