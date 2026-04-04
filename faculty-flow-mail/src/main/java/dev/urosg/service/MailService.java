package dev.urosg.service;

public interface MailService {
	void sendNewAccountMail(String to, String username, String verificationUrl);
}
