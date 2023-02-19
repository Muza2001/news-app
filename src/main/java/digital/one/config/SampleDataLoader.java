package digital.one.config;

import digital.one.model.User;
import digital.one.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
public class SampleDataLoader implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(SampleDataLoader.class);
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;


    public SampleDataLoader(PasswordEncoder passwordEncoder,
                            UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Loading faker data");

        // create 1 account
        User user = new User(
                "Muzaffar",
                passwordEncoder.encode("123"),
                "muza",
                Instant.now(),
                true);
        if (!userRepository.existsByUsername(user.getUsername()))
            userRepository.save(user);
    }
}
