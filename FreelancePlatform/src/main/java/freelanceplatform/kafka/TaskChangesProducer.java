package freelanceplatform.kafka;

import freelanceplatform.kafka.topics.TaskChangesTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskChangesProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TaskChangesProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, TaskChangesTopic topic) {
        log.info(String.format("event - %s", message));
        kafkaTemplate.send(topic.toString(), message);
    }
}
