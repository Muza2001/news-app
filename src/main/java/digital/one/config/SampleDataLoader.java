package digital.one.config;

import com.github.javafaker.Faker;
import digital.one.model.Category;
import digital.one.model.News;
import digital.one.model.User;
import digital.one.repository.CategoryRepository;
import digital.one.repository.NewsRepository;
import digital.one.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Component
public class SampleDataLoader implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(SampleDataLoader.class);
    private final NewsRepository newsRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final Faker faker;

    public SampleDataLoader(NewsRepository newsRepository,
                            PasswordEncoder passwordEncoder,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository, Faker faker) {
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.faker = faker;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Loading faker data");

        // create 1 account

        userRepository.save(new User(
                "Muzaffar Mahmudov",
                passwordEncoder.encode("123"),
                "muza",
                "muza5660@gmail.com",
                Instant.now(),
                true
        ));

        categoryRepository.save(new Category("Category"));

        // create 100 rows of news in the database

        Stream<News> news1 = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new News(
                    faker.name().title(),
                    faker.weather().description(),
                        Instant.now(),
                        Instant.now(),
                        faker.internet().url()
                        ));
        newsRepository.saveAll(news1.collect(Collectors.toList()));
    }
}
