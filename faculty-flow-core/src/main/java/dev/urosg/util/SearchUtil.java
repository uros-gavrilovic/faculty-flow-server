package dev.urosg.util;

import dev.urosg.model.dto.SearchRequest;
import dev.urosg.model.dto.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.HashSet;

public class SearchUtil {

	public static Pageable toPageable(SearchRequest<?> searchRequest) {
		return PageRequest.of(
			searchRequest.page(),
			searchRequest.size(),
			Sort.by(searchRequest.direction(), searchRequest.sortBy())
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
