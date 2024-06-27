package freelanceplatform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static freelanceplatform.kafka.topics.TaskChangesTopic.*;
import static freelanceplatform.kafka.topics.UserChangesTopic.UserCreated;

@Configuration
public class KafkaConfig {

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
