package freelanceplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class  FreelancePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreelancePlatformApplication.class, args);

    }

}
