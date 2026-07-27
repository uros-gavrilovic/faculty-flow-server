package dev.urosg.model.dto;

import dev.urosg.model.interfaces.SortableFilter;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record RoomFilter(
	String name,
	String code
) implements SortableFilter {

	@Override
	public String defaultSortBy() {
		return "name";
	}
}
