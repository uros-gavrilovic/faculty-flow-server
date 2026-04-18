package dev.urosg;

import config.KafkaConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(KafkaConsumerConfig.class)
public class FacultyFlowMail {
	static void main() {
		SpringApplication.run(FacultyFlowMail.class);
	}
}
