package notificationService.consumers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TaskChangesConsumer {

    private final ObjectMapper mapper;

    @Autowired
    public TaskChangesConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void consumeTaskChange(String message) {
        log.info("Received message: {}", message);
        //todo
    }
}
