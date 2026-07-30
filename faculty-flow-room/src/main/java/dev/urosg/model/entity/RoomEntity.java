package dev.urosg.model.entity;

import dev.urosg.model.enumeration.Building;
import dev.urosg.model.enumeration.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@Table(name = "room")
public class RoomEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ColumnDefault("gen_random_uuid()")
	@Column(name = "uuid")
	private UUID uuid;

	@Size(max = 255)
	@NotNull
	@Column(name = "name", nullable = false)
	private String name;

	@Size(max = 255)
	@NotNull
	@Column(name = "code", nullable = false)
	private String code;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "type")
	private RoomType type;

	@Column(name = "floor")
	private Integer floor;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "building")
	private Building building;

	@Size(max = 255)
	@Column(name = "old_name")
	private String oldName;

	@Column(name = "capacity")
	private Integer capacity;

}
