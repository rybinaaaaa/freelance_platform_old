package notificationService.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import notificationService.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper mapper;

    @Autowired
    public UserConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "user_created",
            groupId = "myGroup"
    )
    public void consumeUser(String message) {
        try {
            Map<String, Object> userMap = mapper.readValue(message, Map.class);

            // Извлечение данных по ключам
            String email = (String) userMap.get("email");
            String username = (String) userMap.get("username");

            LOGGER.info(String.format("Message received for user %s, with email - %s", username, email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
