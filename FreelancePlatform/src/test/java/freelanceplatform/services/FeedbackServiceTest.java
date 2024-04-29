package freelanceplatform.services;
import freelanceplatform.model.Feedback;
import freelanceplatform.utils.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Feedback> feedbackById = feedbackService.findById(1L);
        assertTrue(feedbackById.isPresent());
        Assertions.assertThat(feedbackById.get().getSender().getEmail()).isEqualTo("user5@example.com");
    }

    @Test
    void deleteByIdTest() {
        feedbackService.deleteById(1L);
        assertFalse(feedbackService.findById(1L).isPresent());
    }
}