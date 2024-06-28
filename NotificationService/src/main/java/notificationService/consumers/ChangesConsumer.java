package notificationService.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationService.service.EmailSenderService;
import notificationService.service.NotificationSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public abstract class ChangesConsumer {

    protected final ObjectMapper mapper;

    protected final NotificationSender notificationSender;

    protected final EmailSenderService emailSenderService;

    protected final WebClient webClient;

    @Autowired
    public ChangesConsumer(ObjectMapper mapper, NotificationSender notificationSender, EmailSenderService emailSenderService, WebClient webClient) {
        this.mapper = mapper;
        this.notificationSender = notificationSender;
        this.emailSenderService = emailSenderService;
        this.webClient = webClient;
    }

    abstract void consumeChange(ConsumerRecord<String, String> record) throws JsonProcessingException;
}
