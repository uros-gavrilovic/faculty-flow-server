package dev.urosg.service.impl;

import dev.urosg.adapter.RoomAdapter;
import dev.urosg.model.dto.Room;
import dev.urosg.model.dto.RoomFilter;
import dev.urosg.model.dto.SearchRequest;
import dev.urosg.model.dto.SearchResponse;
import dev.urosg.model.entity.RoomEntity;
import dev.urosg.repository.RoomRepository;
import dev.urosg.repository.specification.RoomSpecifications;
import dev.urosg.service.RoomService;
import dev.urosg.util.SearchUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

@Slf4j
@Transactional
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

	@Override
	public SearchResponse<Room> searchRooms(SearchRequest<RoomFilter> request) {
		Page<Room> page = roomRepository.findAll(
			RoomSpecifications.toSpecification(request.filter()),
			SearchUtil.toPageable(request)
		);

		return SearchUtil.toSearchResponse(page);
	}

	@Override
	public Room getRoom(UUID uuid) {
		return RoomAdapter.toDto(findByUuid(uuid));
	}

	@Override
	public Room createRoom(Room room) {
		RoomEntity entity = RoomAdapter.toEntity(room);

		entity.setUuid(UUID.randomUUID());

		RoomEntity createdEntity = roomRepository.saveAndFlush(entity);
		log.info("Created room '{}' ({})", createdEntity.getName(), createdEntity.getUuid());

		return RoomAdapter.toDto(createdEntity);
	}

	@Override
	public Room updateRoom(Room room) {
		RoomEntity roomEntity = findByUuid(room.uuid());

		roomEntity.setName(room.name());
		roomEntity.setCode(room.code());
		roomEntity.setType(room.type());
		roomEntity.setFloor(room.floor());
		roomEntity.setBuilding(room.building());
		roomEntity.setOldName(room.oldName());
		roomEntity.setCapacity(room.capacity());

		RoomEntity updatedEntity = roomRepository.saveAndFlush(roomEntity);
		log.info("Updated room '{}' ({})", updatedEntity.getName(), updatedEntity.getUuid());

		return RoomAdapter.toDto(updatedEntity);
	}

	@Override
	public void deleteRoom(UUID uuid) {
		RoomEntity roomToDelete = findByUuid(uuid);
		log.info("Deleting room '{}' ({})", roomToDelete.getName(), roomToDelete.getUuid());

		roomRepository.deleteByUuid(uuid);
	}

	private @NonNull RoomEntity findByUuid(UUID uuid) {
		return roomRepository.findByUuid(uuid)
			.orElseThrow(() -> new IllegalArgumentException("Room with UUID '" + uuid + "' not found"));
	}

}
