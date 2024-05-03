package freelanceplatform.services;
import freelanceplatform.model.Feedback;
import freelanceplatform.utils.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackServiceTest extends IntegrationTestBase {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackServiceTest(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Test
    void findByIdTest() {
        Optional<Feedback> feedbackById = feedbackService.findById(1);
        assertTrue(feedbackById.isPresent());
        Assertions.assertThat(feedbackById.get().getSender().getEmail()).isEqualTo("user5@example.com");
    }

    @Test
    void deleteByIdTest() {
        feedbackService.deleteById(1);
        assertFalse(feedbackService.findById(1).isPresent());
    }
}