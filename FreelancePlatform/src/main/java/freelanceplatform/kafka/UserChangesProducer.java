package freelanceplatform.kafka;

import freelanceplatform.kafka.topics.UserChangesTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserChangesProducer {

//    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, UserChangesTopic topic) {
//        LOGGER.info(String.format("event - %s", message));
        log.info(String.format("event - %s", message));
        kafkaTemplate.send(topic.toString(), message);
    }
}
