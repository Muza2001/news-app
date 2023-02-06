package digital.one;

import digital.one.model.User;
import digital.one.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
public class NewsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsAppApplication.class, args);
    }



}
