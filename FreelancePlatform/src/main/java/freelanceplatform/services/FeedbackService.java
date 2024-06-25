package freelanceplatform.services;

import freelanceplatform.data.FeedbackRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing feedback.
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"feedbacks"})
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    /**
     * Updates an existing feedback.
     *
     * @param newFb the feedback to update
     * @return the updated feedback
     */
    @Transactional
    @CachePut(key = "#newFb.id")
    public Feedback update(Feedback newFb) {
        log.info("Updating feedback with id {}", newFb.getId());
        return feedbackRepository.findById(newFb.getId()).map(fb -> {
            fb.setComment(newFb.getComment());
            fb.setRating(newFb.getRating());
            return feedbackRepository.save(fb);
        }).orElseThrow(() -> new NotFoundException("Feedback with id " + newFb.getId() + " not found."));
    }

    /**
     * Saves a new feedback.
     *
     * @param feedback the feedback to save
     * @return the saved feedback
     */
    @Transactional
    @CachePut(key = "#feedback.id")
    public Feedback save(Feedback feedback) {
        log.info("Saving new feedback with id {}", feedback.getId());
        Optional.ofNullable(feedback.getReceiver())
                .flatMap(maybeReceiver -> Optional.of(userService.find(maybeReceiver.getId())))
                .ifPresent(user -> user.addReceivedFeedback(feedback));
        Optional.ofNullable(feedback.getSender())
                .flatMap(maybeSender -> Optional.of(userService.find(maybeSender.getId())))
                .ifPresent(user -> user.addSentFeedback(feedback));
        return feedbackRepository.save(feedback);
    }

    /**
     * Finds a feedback by its ID.
     *
     * @param id the ID of the feedback
     * @return an Optional containing the found feedback, or empty if not found
     */
    @Transactional(readOnly = true)
    @Cacheable
    public Optional<Feedback> findById(Integer id) {
        log.info("Finding feedback by id {}", id);
        return feedbackRepository.findById(id);
    }

    /**
     * Finds all feedbacks.
     *
     * @return a list of all feedbacks
     */
    @Transactional(readOnly = true)
    public List<Feedback> findAll() {
        log.info("Finding all feedbacks");
        return feedbackRepository.findAll();
    }

    /**
     * Deletes a feedback by its ID.
     *
     * @param id the ID of the feedback to delete
     * @return true if the feedback was deleted, false otherwise
     */
    @Transactional
    @CacheEvict
    public boolean deleteById(Integer id) {
        log.info("Deleting feedback with id {}", id);
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    User sender = feedback.getSender();
                    User receiver = feedback.getReceiver();

                    sender.deleteSentFeedback(feedback);
                    receiver.deleteReceivedFeedback(feedback);

                    feedback.setSender(null);
                    feedback.setReceiver(null);

                    feedbackRepository.delete(feedback);
                    return true;
                }).orElse(false);
    }

    /**
     * Finds feedbacks by their receiver.
     *
     * @param receiver the receiver of the feedbacks
     * @return a list of feedbacks received by the given user
     */
    @Transactional(readOnly = true)
    public List<Feedback> findByReceiver(User receiver) {
        log.info("Finding feedbacks by receiver {}", receiver.getUsername());
        return feedbackRepository.findByReceiver(receiver);
    }

    /**
     * Finds feedbacks by their sender.
     *
     * @param sender the sender of the feedbacks
     * @return a list of feedbacks sent by the given user
     */
    @Transactional(readOnly = true)
    public List<Feedback> findBySender(User sender) {
        log.info("Finding feedbacks by sender {}", sender.getUsername());
        return feedbackRepository.findBySender(sender);
    }
}
