package freelanceplatform.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskCreatedProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TaskCreatedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        LOGGER.info(String.format("event - %s", message));
        String topic = "task_created";
        kafkaTemplate.send(topic, message);
    }
}
