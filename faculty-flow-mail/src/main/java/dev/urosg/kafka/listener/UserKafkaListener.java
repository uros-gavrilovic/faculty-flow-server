package dev.urosg.kafka.listener;

import dev.urosg.kafka.KafkaTopics;
import dev.urosg.model.event.UserAccountVerificationEvent;
import dev.urosg.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserKafkaListener {

	private final MailService mailService;

	@KafkaListener(
		topics = KafkaTopics.USER_ACCOUNT_VERIFICATION,
		groupId = "mail-service"
	)
	public void handle(UserAccountVerificationEvent event) {
		mailService.sendNewAccountMail(
			event.email(),
			event.username(),
			event.verificationUrl()
		);
	}
}