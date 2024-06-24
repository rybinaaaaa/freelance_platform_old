package freelanceplatform.services;

import freelanceplatform.data.FeedbackRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing feedback.
 */
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    /**
     * Updates an existing feedback.
     *
     * @param newFb the feedback to update
     * @return the updated feedback
     */
    @Transactional
    public Feedback update(Feedback newFb) {
        return feedbackRepository.findById(newFb.getId()).map(fb -> {
            fb.getReceiver().deleteReceivedFeedback(fb);
            fb.getSender().deleteSentFeedback(fb);

            Optional.ofNullable(newFb.getReceiver()).ifPresent(receiver -> {
                receiver.addReceivedFeedback(newFb);
            });
            Optional.ofNullable(newFb.getSender()).ifPresent(sender -> {
                sender.addSentFeedback(newFb);
            });

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
    public Feedback save(Feedback feedback) {
        Optional.ofNullable(feedback.getReceiver())
                .flatMap(maybeReceiver -> this.getUser(maybeReceiver.getId()))
                .ifPresent(user -> user.addReceivedFeedback(feedback));
        Optional.ofNullable(feedback.getSender())
                .flatMap(maybeSender -> this.getUser(maybeSender.getId()))
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
    public Optional<Feedback> findById(Integer id) {
        return feedbackRepository.findById(id);
    }

    /**
     * Finds all feedbacks.
     *
     * @return a list of all feedbacks
     */
    @Transactional(readOnly = true)
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    /**
     * Deletes a feedback by its ID.
     *
     * @param id the ID of the feedback to delete
     * @return true if the feedback was deleted, false otherwise
     */
    @Transactional
    public boolean deleteById(Integer id) {
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
        return feedbackRepository.findBySender(sender);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return an Optional containing the found user, or empty if not found
     */
    @Transactional(readOnly = true)
    protected Optional<User> getUser(Integer id) {
        return Optional.ofNullable(id).flatMap(userRepository::findById);
    }
}
