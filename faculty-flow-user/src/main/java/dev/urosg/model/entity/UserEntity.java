package dev.urosg.model.entity;

import java.util.UUID;
import java.util.Set;
import dev.urosg.model.constant.PatternConstant;
import dev.urosg.model.enumeration.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "user_account")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ColumnDefault("gen_random_uuid()")
	@Column(name = "uuid")
	private UUID uuid;

	@NotNull
	@Pattern(regexp = PatternConstant.USERNAME_PATTERN)
	@Size(min = PatternConstant.USERNAME_MIN_LENGTH, max = PatternConstant.USERNAME_MAX_LENGTH)
	@Column(nullable = false, unique = true)
	private String username;

	@NotNull
	@Pattern(regexp = PatternConstant.PASSWORD_PATTERN)
	@Size(min = PatternConstant.PASSWORD_LENGTH, max = PatternConstant.PASSWORD_LENGTH)
	private String password;

	@Column(unique = true)
	@Pattern(regexp = PatternConstant.EMAIL_PATTERN)
	private String email;

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;

	@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
	@CollectionTable(
		name = "user_roles",
		joinColumns = @JoinColumn(name = "user_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Set<UserRole> roles;
}
