package dev.urosg.kafka.producer;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.model.event.UserAccountVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendVerificationEvent(String email, String username, String url) {

		UserAccountVerificationEvent event = new UserAccountVerificationEvent(email, username, url);

		kafkaTemplate.send(
			KafkaTopics.USER_ACCOUNT_VERIFICATION,
			email,
			event
		);
	}
}