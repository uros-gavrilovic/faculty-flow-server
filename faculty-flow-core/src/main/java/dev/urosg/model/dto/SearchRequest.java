package dev.urosg.model.dto;

import dev.urosg.model.interfaces.SortableFilter;
import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
public record SearchRequest<F extends SortableFilter>(
	Integer page,
	Integer size,
	String sortBy,
	Sort.Direction direction,
	F filter
) {}