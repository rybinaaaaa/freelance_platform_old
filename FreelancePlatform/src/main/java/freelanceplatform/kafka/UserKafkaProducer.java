package freelanceplatform.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserKafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public UserKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        LOGGER.info(String.format("event - %s", message));
        kafkaTemplate.send(topic, message);
    }
}
