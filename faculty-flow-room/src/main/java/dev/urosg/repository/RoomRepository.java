package dev.urosg.repository;

import dev.urosg.model.dto.Room;
import dev.urosg.model.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>,
                                        JpaSpecificationExecutor<Room> {
	Optional<RoomEntity> findByUuid(UUID uuid);
}
