package freelanceplatform.kafka;

import freelanceplatform.kafka.topics.UserChangesTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserChangesProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, UserChangesTopic topic) {
        log.info(String.format("event - %s", message));
        kafkaTemplate.send(topic.toString(), message);
    }
}
