package dev.urosg.repository;

import dev.urosg.model.entity.UserEntity;
import dev.urosg.model.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsername(String username);
	long count();
	Optional<UserEntity> findByUuid(UUID uuid);
	Set<UserEntity> findByRolesContaining(UserRole role);
}
