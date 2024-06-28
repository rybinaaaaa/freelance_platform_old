package notificationService;

import notificationService.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class NotificationServiceApplication {

//    @Autowired
//    private EmailSenderService senderService;


    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void sendEmail(){
//        String toEmail = "dniil2003@gmail.com";
//        senderService.sendEmail(toEmail, "Test Subject", "Test Body");
//    }


}
