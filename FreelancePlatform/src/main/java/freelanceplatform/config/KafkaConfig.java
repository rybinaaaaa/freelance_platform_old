package freelanceplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    /**
     * Creates kafka topic for user creation
     * @return
     */
    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user_created")
                .build();
    }

    /**
     * Creates kafka topic for new task creation
     * @return
     */
    @Bean
    public NewTopic taskCreatedTopic() {
        return TopicBuilder.name("task_created")
                .build();
    }
}
