package mobile.application.footcardz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FootCardzApplication {
    public static void main(String[] args) {
        SpringApplication.run(FootCardzApplication.class, args);
    }
}
