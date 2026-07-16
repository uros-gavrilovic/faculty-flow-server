package dev.urosg.util;

import dev.urosg.model.dto.SearchRequest;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.interfaces.SortableFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.HashSet;

public class SearchUtil {

	public static <F extends SortableFilter> Pageable toPageable(SearchRequest<F> request) {
		return toPageable(request, request.filter());
	}

	private static <F extends SortableFilter> Pageable toPageable(SearchRequest<F> request, F filter) {
		int page = request.page() != null ? request.page() : 0;
		int size = request.size() != null ? request.size() : 10;

		if (filter == null) return PageRequest.of(page, size);

		String sortBy = request.sortBy() != null ? request.sortBy() : filter.defaultSortBy();
		Sort.Direction direction = request.direction() != null ? request.direction() : Sort.Direction.ASC;

		return PageRequest.of(
			page,
			size,
			Sort.by(direction, sortBy)
		);
	}

	public static <T> SearchResponse<T> toSearchResponse(Page<T> page) {
		return new SearchResponse<>(
			new HashSet<>(page.getContent()),
			page.getTotalElements(),
			page.getNumber(),
			page.getSize()
		);
	}
}
