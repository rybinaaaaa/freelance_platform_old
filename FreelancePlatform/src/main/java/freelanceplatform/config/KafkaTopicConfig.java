package freelanceplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name("user_created")
                .build();
    }

    @Bean
    public NewTopic taskCreatedTopic() {
        return TopicBuilder.name("task_created")
                .build();
    }
}
