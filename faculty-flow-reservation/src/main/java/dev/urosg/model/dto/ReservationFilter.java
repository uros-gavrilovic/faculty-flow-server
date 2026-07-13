package dev.urosg.model.dto;

import dev.urosg.model.enumeration.ReservationStatus;
import dev.urosg.model.interfaces.SortableFilter;
import lombok.Builder;
import lombok.With;
import java.time.LocalDateTime;

@Builder
@With
public record ReservationFilter (
	String name,
	String room,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String reservedBy,
	String reviewedBy,
	ReservationStatus status
) implements SortableFilter {

	@Override
	public String defaultSortBy() {
		return "startTime";
	}
}