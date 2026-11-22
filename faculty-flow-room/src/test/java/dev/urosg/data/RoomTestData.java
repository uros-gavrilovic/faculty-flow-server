package dev.urosg.data;

import dev.urosg.adapter.RoomAdapter;
import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.RoomFilter;
import dev.urosg.model.entity.RoomEntity;
import dev.urosg.model.enumeration.Building;
import dev.urosg.model.enumeration.RoomType;
import java.util.List;
import java.util.UUID;

public final class RoomTestData {

	public static final UUID CLASSROOM_UUID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
	public static final UUID COMPUTER_LAB_UUID = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000003");

	public static Room CLASSROOM() {
		return new Room(CLASSROOM_UUID, "Sala 08", "08", RoomType.CLASSROOM, -1, Building.OLD, "005", 30);
	}

	public static Room COMPUTER_LAB() {
		return new Room(COMPUTER_LAB_UUID, "Sala 06", "06", RoomType.COMPUTER_LAB, -1, Building.OLD, "C001", 25);
	}

}
