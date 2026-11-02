package dev.urosg;

import dev.urosg.annotation.EnableJwtSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJwtSecurity
public class FacultyFlowReservation {
	static void main() {
		SpringApplication.run(FacultyFlowReservation.class);
	}
}
