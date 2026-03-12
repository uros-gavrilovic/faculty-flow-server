package dev.urosg.service.impl;

import dev.urosg.adapter.RoomAdapter;
import dev.urosg.model.dto.Room;
import dev.urosg.model.entity.RoomEntity;
import dev.urosg.repository.RoomRepository;
import dev.urosg.service.RoomService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	public RoomServiceImpl(
		RoomRepository roomRepository
	) {
		this.roomRepository = roomRepository;
	}

	@Override
	public List<Room> getAllRooms() {
		List<RoomEntity> roomEntities = roomRepository.findAll();

		return roomEntities.stream()
			.map(RoomAdapter::toDto)
			.toList();
	}
}
