package notificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//import java.util.logging.Logger;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(
            topics = "user_created",
            groupId = "myGroup"
    )
    public void consume(String eventMessage) {
        LOGGER.info(String.format("Message received - %s", eventMessage));
    }
}
