package dev.urosg.model.dto;

import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
public record SearchRequest<T>(
	int page,
	int size,
	String sortBy,
	Sort.Direction direction,
	T filter
) {}
