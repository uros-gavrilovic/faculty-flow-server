package dev.urosg.repository.specification;

import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.RoomFilter;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public final class RoomSpecifications {

	private RoomSpecifications() {}

	public static Specification<Room> toSpecification(RoomFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter != null) {
				if (StringUtils.hasText(filter.name())) {
					predicates.add(cb.like(
						cb.lower(root.get("name")),
						"%" + filter.name().toLowerCase() + "%"
					));
				}

				if (StringUtils.hasText(filter.code())) {
					predicates.add(cb.like(
						cb.lower(root.get("code")),
						"%" + filter.code().toLowerCase() + "%"
					));
				}
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
