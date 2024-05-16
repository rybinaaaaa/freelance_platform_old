package freelanceplatform.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserCreatedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        LOGGER.info(String.format("event - %s", message));
        String topic = "user_created";
        kafkaTemplate.send(topic, message);
    }
}
