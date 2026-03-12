package dev.urosg.adapter;

import dev.urosg.model.dto.Room;
import dev.urosg.model.entity.RoomEntity;

public class RoomAdapter {
	public static Room toDto(RoomEntity entity) {
		return new Room(
		entity.getUuid(),
		entity.getName(),
		entity.getCode(),
		entity.getType(),
		entity.getFloor(),
		entity.getBuilding(),
		entity.getOldName(),
		entity.getCapacity()
	);
	}

	public static RoomEntity toEntity(Room dto) {
		RoomEntity entity = new RoomEntity();

		entity.setUuid(dto.uuid());
		entity.setName(dto.name());
		entity.setCode(dto.code());
		entity.setType(dto.type());
		entity.setFloor(dto.floor());
		entity.setBuilding(dto.building());
		entity.setOldName(dto.oldName());
		entity.setCapacity(dto.capacity());

		return entity;
	}
}
