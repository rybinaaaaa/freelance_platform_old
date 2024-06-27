package freelanceplatform.kafka;

import freelanceplatform.kafka.topics.UserChangesTopic;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCreatedProducer {

//    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserCreatedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, UserChangesTopic topic) {
//        LOGGER.info(String.format("event - %s", message));
        log.info(String.format("event - %s", message));
        kafkaTemplate.send(topic.toString(), message);
    }
}
