package inhagdsc.mamasteps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MamastepsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MamastepsApplication.class, args);
	}

}
