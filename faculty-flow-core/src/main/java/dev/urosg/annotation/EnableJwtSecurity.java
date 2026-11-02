package dev.urosg.annotation;

import dev.urosg.config.AuthenticationUtilsConfig;
import dev.urosg.security.JwtSecurityConfig;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * Turns on JWT authentication and {@code @PreAuthorize} for a service. The service must also depend
 * on {@code spring-boot-starter-security} and define {@code jwt.secret}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
	JwtSecurityConfig.class, // Initializes JWT security filter
	AuthenticationUtilsConfig.class // Initializes authentication utilities
})
public @interface EnableJwtSecurity {}
