package dev.urosg.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Shared {@code messageSource} for every microservice. Language packs are looked up in order:
 * <ol>
 *   <li>{@code i18n/messages_<locale>.properties} - owned by each microservice (may override core keys)</li>
 *   <li>{@code i18n/core-messages_<locale>.properties} - owned by core</li>
 * </ol>
 * The locale is resolved from the request's {@code Accept-Language} header.
 */
@Configuration
public class MessageSourceConfig {

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	public static final String SERVICE_BASENAME = "i18n/messages";
	public static final String CORE_BASENAME = "i18n/core-messages";

	@Bean
	public MessageSource messageSource() {
		return createMessageSource();
	}

	public static MessageSource createMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

		messageSource.setBasenames(SERVICE_BASENAME, CORE_BASENAME);
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setDefaultLocale(DEFAULT_LOCALE);
		messageSource.setFallbackToSystemLocale(false);

		return messageSource;
	}
}
