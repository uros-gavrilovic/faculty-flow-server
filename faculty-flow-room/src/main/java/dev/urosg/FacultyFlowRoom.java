package dev.urosg;

import dev.urosg.annotation.EnableJwtSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJwtSecurity
public class FacultyFlowRoom {
    static void main() {
        SpringApplication.run(FacultyFlowRoom.class);
    }
}
