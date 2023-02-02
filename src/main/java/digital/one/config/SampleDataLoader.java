package digital.one.config;

import com.github.javafaker.Faker;
import digital.one.model.News;
import digital.one.model.User;
import digital.one.repository.NewsRepository;
import digital.one.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;


@Component
@RequiredArgsConstructor
public class SampleDataLoader implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(SampleDataLoader.class);
    private final NewsRepository newsRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final Faker faker;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Loading faker data");

        // create 1 account

        /**
         * private String full_name;
         *
         *     private String password;
         *
         *     @Column(unique = true, name = "username")
         *     private String username;
         *
         *     @Column(unique = true, name = "email")
         *     private String email;
         *
         *     private Instant expiration;
         *
         *     private Boolean isEnabled;
         */

        userRepository.save(new User(
                "Muzaffar Mahmudov",
                passwordEncoder.encode("123"),
                "muza",
                "muza5660@gmail.com",
                Instant.now(),
                true
        ));

        // create 100 rows of news in the database

        List<News> news1 = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new News(
                    faker.name().title(),
                    faker.weather().description(),
                        Instant.now(),
                        Instant.now(),
                        faker.internet().url()
                        )).toList();
        newsRepository.saveAll(news1);
    }
}
