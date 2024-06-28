package notificationService.consumers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import notificationService.notificationStrategies.SendAllUsersStrategy;
import notificationService.notificationStrategies.SendCustomerStrategy;
import notificationService.notificationStrategies.SendEmailStrategy;
import notificationService.notificationStrategies.SendFreelancerStrategy;
import notificationService.service.EmailSenderService;
import notificationService.service.NotificationSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class TaskChangesConsumer extends ChangesConsumer {

    @Autowired
    public TaskChangesConsumer(ObjectMapper mapper, NotificationSender notificationSender, EmailSenderService emailSenderService, WebClient webClient) {
        super(mapper, notificationSender, emailSenderService, webClient);
    }

    @KafkaListener(
            topics = {"task_posted", "freelancer_assigned", "task_accepted", "freelancer_removed", "task_send_on_review"},
            groupId = "myGroup"
    )
    public void consumeChange(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String taskJson = record.value();
        log.info("Received message: {}", taskJson);
        String topic = record.topic();
        String taskTitle = mapper.readTree(taskJson).get("title").asText();
        String freelancerUsername = "";
        String customerUsername = "";

        if (!mapper.readTree(taskJson).get("freelancer").asText().equals("null"))
            freelancerUsername = mapper.readTree(taskJson).get("freelancer").get("username").asText();
        if (!mapper.readTree(taskJson).get("customer").asText().equals("null"))
            customerUsername = mapper.readTree(taskJson).get("customer").get("username").asText();
        SendEmailStrategy sendEmailStrategy;
        switch (topic) {
            case "task_posted" -> {
                sendEmailStrategy = new SendAllUsersStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        taskJson,
                        null,
                        "New task was posted!",
                        String.format("Task: '%s' was posted recently. This opportunity could be perfect for you!", taskTitle));
            }
            case "freelancer_assigned" -> {
                sendEmailStrategy = new SendFreelancerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        taskJson,
                        null,
                        "You have been assigned to a task!",
                        String.format("We are pleased to inform you that you have been assigned to a task '%s'", taskTitle));
            }
            case "task_accepted" -> {
                sendEmailStrategy = new SendFreelancerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        taskJson,
                        null,
                        "Congratulations! One of your completed tasks has been accepted",
                        String.format("We are pleased to inform you that one of your completed tasks '%s' has been accepted by the customer.", taskTitle));
            }
            case "freelancer_removed" -> {
                sendEmailStrategy = new SendFreelancerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        taskJson,
                        null,
                        "We are sorry! You were removed as task assignee!",
                        String.format("We are sorry to inform you that you were removed as task assignee from task '%s'", taskTitle));
            }
            case "task_send_on_review" -> {
                sendEmailStrategy = new SendCustomerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        taskJson,
                        null,
                        "One of yor tasks was send on review!",
                        String.format("We wanted to inform you that the freelancer '%s' has submitted the task '%s' for your review", freelancerUsername, taskTitle));
            }
            default -> {
                throw new IllegalArgumentException("Unsupported topic: " + topic);
            }
        }
    }
}
