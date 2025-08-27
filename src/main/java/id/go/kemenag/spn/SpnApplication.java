package id.go.kemenag.spn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SpnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpnApplication.class, args);
	}

}
