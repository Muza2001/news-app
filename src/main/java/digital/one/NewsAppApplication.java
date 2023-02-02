package digital.one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("digital.one.controller")
public class NewsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsAppApplication.class, args);
    }




}
