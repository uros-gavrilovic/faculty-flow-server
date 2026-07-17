package dev.urosg.loader;

import com.github.javafaker.Faker;
import dev.urosg.model.entity.ReservationEntity;
import dev.urosg.model.enumeration.EventType;
import dev.urosg.model.enumeration.ReservationStatus;
import dev.urosg.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MockReservationLoader {

	@Value("${dataloader.init-test-data:false}")
	private boolean initTestData;

	private final Faker faker = new Faker();

	@Autowired
	private ReservationRepository reservationRepository;

	private static final Logger log = LoggerFactory.getLogger(MockReservationLoader.class);

	@PostConstruct
	void loadTestData() {
		if (initTestData) {
			log.warn("Loading test data ...");

			LocalDateTime thisMonday = LocalDateTime.now().with(DayOfWeek.MONDAY);
			LocalDateTime thisFriday = LocalDateTime.now().with(DayOfWeek.FRIDAY);

			int reservationsThisWeekCount = reservationRepository
				.countByStartTimeLessThanAndEndTimeGreaterThan(thisMonday, thisFriday);
			if (reservationsThisWeekCount == 0) {
				loadReservations(10);
			}
		}
	}

	private void loadReservations(int counter) {

		List<EventType> eventTypes = List.of(EventType.values());
		List<ReservationStatus> statuses = List.of(ReservationStatus.values());
		List<String> rooms = List.of("06", "07", "08", "09", "AM1", "AM2");
		List<String> reservedBy = List.of("uros.gavrilovic", "andrea.tomas");
		List<String> subjects = List.of(
			"Algorithms",
			"Data Structures",
			"Database Systems",
			"Operating Systems",
			"Computer Networks",
			"Software Engineering",
			"Object-Oriented Programming",
			"Web Development"
		);


		for (int i = 0; i < counter; i++) {

			ReservationEntity r = new ReservationEntity();

			ReservationStatus status = faker.options().nextElement(statuses);
			LocalDateTime startTime = randomStartTimeThisWeek();

			r.setUuid(UUID.randomUUID());
			r.setName(faker.options().nextElement(subjects));
			r.setEventType(faker.options().nextElement(eventTypes));
			r.setRoom(faker.options().nextElement(rooms));
			r.setStartTime(startTime);
			r.setEndTime(randomEndTime(startTime));
			r.setStatus(status);
			r.setNote(faker.lorem().sentence());
			r.setReservedBy(faker.options().nextElement(reservedBy));

			if (!ReservationStatus.PENDING.equals(status)) {
				r.setReviewedBy("admin");
				if (ReservationStatus.REJECTED.equals(status)) r.setComment(faker.chuckNorris().fact());
			}

			log.info("Created new mock reservation: {}", r);

			reservationRepository.saveAndFlush(r);
		}
	}


	private LocalDateTime randomStartTimeThisWeek() {
		LocalDateTime monday = LocalDate.now()
			.with(DayOfWeek.MONDAY)
			.atStartOfDay();

		int randomDay = faker.number().numberBetween(0, 5); // Mon-Fri
		int randomHour = faker.number().numberBetween(8, 16); // 08:00-16:00

		return monday
			.plusDays(randomDay)
			.plusHours(randomHour);
	}

	private LocalDateTime randomEndTime(LocalDateTime startTime) {
		int duration = faker.number().numberBetween(1, 4); // 1-3 hours
		return startTime.plusHours(duration);
	}
}
