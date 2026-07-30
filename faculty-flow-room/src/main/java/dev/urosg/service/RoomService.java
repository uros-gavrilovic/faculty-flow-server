package dev.urosg.service;

import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.RoomFilter;
import dev.urosg.model.dto.SearchRequest;
import dev.urosg.model.dto.SearchResponse;
import java.util.List;
import java.util.UUID;

public interface RoomService {
	List<Room> getAllRooms();
	SearchResponse<Room> searchRooms(SearchRequest<RoomFilter> request);
	Room getRoom(UUID uuid);
	Room createRoom(Room room);
	Room updateRoom(Room room);
	void deleteRoom(UUID uuid);
}
