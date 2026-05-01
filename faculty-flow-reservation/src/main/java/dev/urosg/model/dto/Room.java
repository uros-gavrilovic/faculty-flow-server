package dev.urosg.model.dto;

import java.util.UUID;

public record Room(
	UUID uuid,
	String name,
	String code
) {
}
