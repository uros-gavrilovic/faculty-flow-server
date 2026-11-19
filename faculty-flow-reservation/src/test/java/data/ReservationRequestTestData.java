package data;

import dev.urosg.model.dto.ReservationRequest;
import dev.urosg.model.enumeration.EventType;
import java.time.LocalDateTime;

public class ReservationRequestTestData {

	public static final LocalDateTime START_TIME = LocalDateTime.of(2026, 11, 20, 10, 0);
	public static final LocalDateTime END_TIME = START_TIME.plusHours(2);

	private static ReservationRequest generateRequest() {
		return ReservationRequest
			.builder()
			.name("Algorithms lecture")
			.roomCode("A101")
			.startTime(START_TIME)
			.endTime(END_TIME)
			.eventType(EventType.LECTURE)
			.reservedBy("john.doe")
			.note("Bring a projector")
			.build();
	}

	public static ReservationRequest VALID_REQUEST() {
		return generateRequest();
	}

	public static ReservationRequest MINIMAL_REQUEST() {
		return generateRequest()
			.toBuilder()
			.reservedBy(null)
			.note(null)
			.build();
	}
}
