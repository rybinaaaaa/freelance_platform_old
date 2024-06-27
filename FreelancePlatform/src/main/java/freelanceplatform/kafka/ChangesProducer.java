package freelanceplatform.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChangesProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, T topic) {
        log.info(String.format("event - %s", message));
        kafkaTemplate.send(topic.toString(), message);
    }
}
