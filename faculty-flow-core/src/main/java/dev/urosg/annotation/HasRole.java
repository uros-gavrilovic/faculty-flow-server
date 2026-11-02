package dev.urosg.annotation;

import dev.urosg.model.enumeration.UserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('{value}'.split(','))")
public @interface HasRole {
	UserRole[] value();
}
