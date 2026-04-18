package dev.urosg.kafka.listener;

import dev.urosg.kafka.event.UserAccountVerificationRequestedEvent;
import dev.urosg.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailKafkaListener {

	private final MailService mailService;

	@KafkaListener(
		topics = "user.account.verification.requested",
		groupId = "mail-service"
	)
	public void handle(UserAccountVerificationRequestedEvent event) {
		mailService.sendNewAccountMail(
			event.email(),
			event.username(),
			event.verificationUrl()
		);
	}
}