package data;

import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.EventType;
import dev.urosg.model.enumeration.ReservationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationTestData {

	public static final UUID RESERVATION_UUID = UUID.fromString("22222222-2222-2222-2222-222222222222");
	public static final LocalDateTime START_TIME = LocalDateTime.of(2026, 11, 20, 10, 0);
	public static final LocalDateTime END_TIME = START_TIME.plusHours(1);

	private static ReservationEntity generateReservation() {
		return ReservationEntity
			.builder()
			.id(1L)
			.uuid(RESERVATION_UUID)
			.name("Software Architecture 101")
			.room("AMF-1")
			.startTime(START_TIME)
			.endTime(END_TIME)
			.reservedBy("john.doe")
			.reviewedBy("admin")
			.eventType(EventType.LECTURE)
			.status(ReservationStatus.ACCEPTED)
			.note("Chuck Norris")
			.comment("Chuck Norris was here")
			.build();
	}

	public static ReservationEntity ACCEPTED_RESERVATION() {
		return generateReservation();
	}

	public static ReservationEntity REJECTED_RESERVATION() {
		return generateReservation()
			.toBuilder()
			.status(ReservationStatus.REJECTED)
			.build();
	}

}
