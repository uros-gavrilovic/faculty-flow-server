package dev.urosg.controller;

import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.RoomFilter;
import dev.urosg.model.dto.SearchRequest;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/room")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@GetMapping
	public List<Room> getAllRooms() {
		return roomService.getAllRooms();
	}

	@PostMapping
	public SearchResponse<Room> searchRooms(SearchRequest<RoomFilter> searchRequest) {
		return roomService.searchRooms(searchRequest);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Room createRoom(@RequestBody Room room) {
		return roomService.createRoom(room);
	}

	@PutMapping()
	public Room updateRoom(@RequestBody Room room) {
		return roomService.updateRoom(room);
	}

	@DeleteMapping("/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRoom(@PathVariable UUID uuid) {
		roomService.deleteRoom(uuid);
	}
}
