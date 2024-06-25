package freelanceplatform.services;
import freelanceplatform.model.Feedback;
import freelanceplatform.utils.CacheableTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackServiceTest extends CacheableTestBase {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackServiceTest(FeedbackService feedbackService, CacheManager cacheManager) {
        this.feedbackService = feedbackService;
        this.cacheManager = cacheManager;
    }

    @Override
    protected String getCacheName() {
        return "feedbacks";
    }

    @AfterEach
    void clearCache() {
        Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
    }

    @Test
    void findByIdTest() {
        int id = 1;

        Feedback feedbackById = feedbackService.findById(id).orElseThrow(IllegalArgumentException::new);
        Assertions.assertThat(feedbackById.getSender().getEmail()).isEqualTo("user5@example.com");

        assertTrue(Optional.ofNullable(cacheManager.getCache(cacheName).get(id)).isPresent());
    }

    @Test
    void deleteByIdTest() {
        int id = 1;

        feedbackService.deleteById(id);
        assertFalse(feedbackService.findById(id).isPresent());

        assertFalse(Optional.ofNullable(cacheManager.getCache("tasks").get(id)).isPresent());
    }

    @Test
    void updateTest() {
        int id = 1;
        Feedback feedback = feedbackService.findById(id).get();

        String comment = "test233";
        feedback.setComment(comment);

        feedbackService.update(feedback);
        assertEquals(feedbackService.findById(id).get().getComment(), comment);

        assertTrue(Optional.ofNullable(cacheManager.getCache(cacheName).get(id)).isPresent());
    }

    @Test
    void testCachingShouldBeAbsentInCache() {
        Optional<Feedback> feedback = feedbackService.findAll().stream().findAny();

        feedback.ifPresent((fb) -> {
            assertFalse(Optional.ofNullable(cacheManager.getCache(cacheName).get(fb.getId())).isPresent());
        });
    }
}