package pl.pjatk.Stepify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.pjatk.Stepify.config.JwtConfig;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class StepifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(StepifyApplication.class, args);
	}

}
