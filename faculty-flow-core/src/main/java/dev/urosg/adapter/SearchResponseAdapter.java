package dev.urosg.adapter;

import dev.urosg.model.dto.SearchResponse;
import org.springframework.data.domain.Page;
import java.util.LinkedHashSet;
import java.util.function.Function;

public final class SearchResponseAdapter {

	private SearchResponseAdapter() {}

	public static <E, D> SearchResponse<D> from(Page<E> page, Function<E, D> mapper) {
		return new SearchResponse<>(
			page.getContent().stream()
				.map(mapper)
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)),
			page.getTotalElements(),
			page.getNumber(),
			page.getSize()
		);
	}
}