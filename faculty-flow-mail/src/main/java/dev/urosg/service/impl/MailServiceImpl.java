package dev.urosg.service.impl;

import dev.urosg.model.PlaceholderConstant;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import dev.urosg.service.MailService;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;

	public MailServiceImpl(
		JavaMailSender mailSender,
		SpringTemplateEngine templateEngine
	) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
	}

	@Override
	public void sendNewAccountMail(String to, String username, String verificationUrl) {
		try {
			Context context = new Context();
			context.setVariable(PlaceholderConstant.USER_NAME, username);
			context.setVariable(PlaceholderConstant.VERIFICATION_URL, verificationUrl);

			String html = templateEngine.process("account/verify-account", context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject("New account created");
			helper.setText(html, true);

			mailSender.send(message);
			log.info("New account '{}' verification e-mail sent to '{}'", username, to);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
