package dev.urosg;

import dev.urosg.config.KafkaProducerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(KafkaProducerConfig.class)
public class FacultyFlowUser {
	static void main() {
		SpringApplication.run(FacultyFlowUser.class);
	}
}
