package dev.urosg;

import dev.urosg.annotation.EnableAuthenticationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAuthenticationUtils
public class FacultyFlowReservation {
	static void main() {
		SpringApplication.run(FacultyFlowReservation.class);
	}
}
