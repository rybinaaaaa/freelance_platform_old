package freelanceplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static freelanceplatform.kafka.topics.TaskChangesTopic.*;
import static freelanceplatform.kafka.topics.UserChangesTopic.UserCreated;

@Configuration
public class KafkaConfig {

//    @Bean
//    public ProducerFactory<String, String> stringProducerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String, String> stringKafkaTemplate() {
//        return new KafkaTemplate<>(stringProducerFactory());
//    }
//
//    @Bean
//    public ProducerFactory<String, Object> jsonProducerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> jsonKafkaTemplate() {
//        return new KafkaTemplate<>(jsonProducerFactory());
//    }

    /**
     * Creates kafka topic for user creation
     * @return
     */
    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder.name(UserCreated.name())
                .build();
    }

    /**
     * Creates kafka topic for new task creation
     * @return
     */
    @Bean
    public NewTopic taskPostedTopic() {
        return TopicBuilder.name(TaskPosted.name())
                .build();
    }

    @Bean
    public NewTopic freelancerAssignedTopic(){
        return TopicBuilder.name(FreelancerAssigned.name())
                .build();
    }

    @Bean
    public NewTopic TaskAcceptedTopic(){
        return TopicBuilder.name(TaskAccepted.name())
                .build();
    }

    @Bean
    public NewTopic freelancerRemovedTopic(){
        return TopicBuilder.name(FreelancerRemoved.name())
                .build();
    }

    @Bean
    public NewTopic taskSendOnReviewTopic(){
        return TopicBuilder.name(TaskSendOnReview.name())
                .build();
    }
}
