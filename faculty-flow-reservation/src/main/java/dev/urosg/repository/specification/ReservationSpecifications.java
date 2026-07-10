package dev.urosg.repository.specification;

import dev.urosg.model.dto.Reservation;
import lombok.NoArgsConstructor;
import dev.urosg.model.dto.ReservationFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public final class ReservationSpecifications {

	public static Specification<Reservation> filter(ReservationFilter filter) {
		return (root, query, cb) -> {
			if (filter == null) return cb.conjunction();

			List<Predicate> predicates = new ArrayList<>();

			if (hasText(filter.name())) {
				predicates.add(cb.like(
					cb.lower(root.get("name")),
					"%" + filter.name().toLowerCase() + "%"
				));
			}

			if (hasText(filter.room())) {
				predicates.add(cb.like(
					cb.lower(root.get("room")),
					"%" + filter.room().toLowerCase() + "%"
				));
			}

			if (filter.startTime() != null) {
				predicates.add(cb.greaterThanOrEqualTo(
					root.get("startTime"),
					filter.startTime()
				));
			}

			if (filter.endTime() != null) {
				predicates.add(cb.lessThanOrEqualTo(
					root.get("endTime"),
					filter.endTime()
				));
			}

			if (hasText(filter.reservedBy())) {
				predicates.add(cb.like(
					cb.lower(root.get("reservedBy")),
					"%" + filter.reservedBy().toLowerCase() + "%"
				));
			}

			if (hasText(filter.reviewedBy())) {
				predicates.add(cb.like(
					cb.lower(root.get("reviewedBy")),
					"%" + filter.reviewedBy().toLowerCase() + "%"
				));
			}

			if (filter.status() != null) {
				predicates.add(cb.equal(
					root.get("status"),
					filter.status()
				));
			}

			return cb.and(predicates.toArray(new Predicate[0])); // toArray -> Object[] -> Predicate[]
		};
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}
}
