package pt.ul.fc.css.soccernow;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;

@SpringBootApplication
public class SoccerNowApplication {

    private static final Logger logger = LoggerFactory.getLogger(SoccerNowApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SoccerNowApplication.class, args);

    }

    @Bean
    @Transactional
    public CommandLineRunner demo() {
        return (args) -> {
            logger.info("do some sanity tests here");
        };
    }
}
