package notificationService.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserChangesConsumer {

    private final ObjectMapper mapper;

    @Autowired
    public UserChangesConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "user_created",
            groupId = "myGroup"
    )
    public void consumeUserChange(String message) {
        try {
            Map<String, Object> userMap = mapper.readValue(message, Map.class);

            // Извлечение данных по ключам
            String email = (String) userMap.get("email");
            String username = (String) userMap.get("username");

            log.info(String.format("Message received for user %s, with email - %s", username, email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
