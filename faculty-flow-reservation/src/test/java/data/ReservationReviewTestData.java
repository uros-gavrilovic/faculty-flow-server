package data;

import dev.urosg.model.dto.ReservationReview;
import dev.urosg.model.enumeration.ReservationStatus;
import java.util.UUID;

public class ReservationReviewTestData {

	public static ReservationReview ACCEPTED_REVIEW(UUID uuid) {
		return ReservationReview
			.builder()
			.uuid(uuid)
			.status(ReservationStatus.ACCEPTED)
			.comment("Chuck Norris is awesome!")
			.build();
	}
}
