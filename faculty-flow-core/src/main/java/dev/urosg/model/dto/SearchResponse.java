package dev.urosg.model.dto;

import java.util.Set;

public record SearchResponse<T>(
	Set<T> data,
	long total,
	int page,
	int size
) {}