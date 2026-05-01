package dev.urosg.adapter;

import dev.urosg.model.dto.Reservation;
import dev.urosg.model.entity.ReservationEntity;

public class ReservationAdapter {
	public static Reservation toDto(ReservationEntity entity) {
		return new Reservation(
			entity.getUuid(),
			entity.getName(),
			entity.getRoom(),
			entity.getStartTime(),
			entity.getEndTime(),
			entity.getReservedBy(),
			entity.getReviewedBy(),
			entity.getStatus(),
			entity.getNote()
		);
	}
}