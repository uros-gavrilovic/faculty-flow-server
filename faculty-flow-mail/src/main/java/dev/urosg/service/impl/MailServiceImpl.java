package dev.urosg.service.impl;

import dev.urosg.model.MailType;
import dev.urosg.model.PlaceholderConstant;
import dev.urosg.model.event.ReservationRequestedEvent;
import dev.urosg.model.event.ReservationReviewedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import dev.urosg.service.MailService;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	@Value("${faculty-flow.service.front-end.url}") private String frontEndUrl;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

	public MailServiceImpl(
		JavaMailSender mailSender,
		SpringTemplateEngine templateEngine
	) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}

	@Override
	public void sendNewAccountMail(String to, String username, String verificationUrl) {
		Context context = new Context();
		context.setVariable(PlaceholderConstant.USER_NAME, username);
		context.setVariable(PlaceholderConstant.VERIFICATION_URL, verificationUrl);

		sendMail(to, context, MailType.VERIFY_ACCOUNT);
		log.info("New account '{}' verification e-mail sent to '{}'", username, to);
	}

	@Override
	public void sendReservationRequestedMail(String to, ReservationRequestedEvent event) {
		Context context = new Context();

		context.setVariable(PlaceholderConstant.NAME, event.name());
		context.setVariable(PlaceholderConstant.ROOM, event.room());
		context.setVariable(PlaceholderConstant.START_TIME, event.startTime().format(formatter));
		context.setVariable(PlaceholderConstant.END_TIME, event.endTime().format(formatter));
		context.setVariable(PlaceholderConstant.RESERVED_BY, event.reservedBy());
		context.setVariable(PlaceholderConstant.NOTE, event.note());
		context.setVariable(PlaceholderConstant.LINK,frontEndUrl + "/reservations?uuid=" + event.uuid());

		sendMail(to, context, MailType.RESERVATION_REQUESTED);
		log.info("Reservation requested e-mail sent to '{}'", to);
	}

	@Override
	public void sendReservationReviewedMail(String to, ReservationReviewedEvent event) {
		Context context = new Context();

		context.setVariable(PlaceholderConstant.NAME, event.name());
		context.setVariable(PlaceholderConstant.ROOM, event.room());
		context.setVariable(PlaceholderConstant.START_TIME, event.startTime().format(formatter));
		context.setVariable(PlaceholderConstant.END_TIME, event.endTime().format(formatter));
		context.setVariable(PlaceholderConstant.REVIEWED_BY, event.reviewedBy());
		context.setVariable(PlaceholderConstant.STATUS, event.status());
		context.setVariable(PlaceholderConstant.COMMENT, event.comment());
		context.setVariable(PlaceholderConstant.LINK,frontEndUrl + "/reservations?uuid=" + event.uuid());

		sendMail(to, context, MailType.RESERVATION_REVIEWED);
		log.info("Reservation reviewed e-mail sent to '{}'", to);
	}


	private void sendMail(String to, Context context, MailType mailType) {
		String html = templateEngine.process(mailType.getTemplatePath(), context);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject(mailType.getSubject());
			helper.setText(html, true);

			mailSender.send(message);
			log.debug("E-mail with subject '{}' sent to '{}'", mailType.getSubject(), to);
		} catch (MailException | MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
