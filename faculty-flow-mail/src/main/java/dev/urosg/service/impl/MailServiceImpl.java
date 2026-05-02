package dev.urosg.service.impl;

import dev.urosg.model.MailType;
import dev.urosg.model.PlaceholderConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
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

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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
	public void sendReservationRequestedMail(
		String to, String name, String room, LocalDateTime startTime, LocalDateTime endTime, String reservedBy, String note
	) {
		Context context = new Context();
		context.setVariable(PlaceholderConstant.NAME, name);
		context.setVariable(PlaceholderConstant.ROOM, room);
		context.setVariable(PlaceholderConstant.START_TIME, startTime.format(formatter));
		context.setVariable(PlaceholderConstant.END_TIME, endTime.format(formatter));
		context.setVariable(PlaceholderConstant.RESERVED_BY, reservedBy);
		context.setVariable(PlaceholderConstant.NOTE, note);

		sendMail(to, context, MailType.RESERVATION_REQUESTED);
		log.info("Reservation requested e-mail sent to '{}'", to);
	}

	@Override
	public void sendReservationReviewedMail(
		String to, String name, String room, LocalDateTime startTime, LocalDateTime endTime, String reviewedBy, String status, String comment
	) {
		Context context = new Context();
		context.setVariable(PlaceholderConstant.NAME, name);
		context.setVariable(PlaceholderConstant.ROOM, room);
		context.setVariable(PlaceholderConstant.START_TIME, startTime.format(formatter));
		context.setVariable(PlaceholderConstant.END_TIME, endTime.format(formatter));
		context.setVariable(PlaceholderConstant.REVIEWED_BY, reviewedBy);
		context.setVariable(PlaceholderConstant.STATUS, status);
		context.setVariable(PlaceholderConstant.COMMENT, comment);

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
