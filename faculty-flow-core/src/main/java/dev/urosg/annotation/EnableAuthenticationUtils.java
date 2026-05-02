package dev.urosg.annotation;

import dev.urosg.config.AuthenticationUtilsConfig;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthenticationUtilsConfig.class)
public @interface EnableAuthenticationUtils {}