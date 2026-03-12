package dev.urosg.model.dto;

import dev.urosg.model.enumeration.Building;
import dev.urosg.model.enumeration.RoomType;
import java.util.UUID;

public record Room(
	UUID uuid,
	String name,
	String code,
	RoomType type,
	Integer floor,
	Building building,
	String oldName,
	Integer capacity
) {
	public Room(UUID uuid, String name, String code, RoomType type, Integer floor, Building building, Integer capacity) {
		this(uuid, name, code, type, floor, building, null, capacity);
	}
}
