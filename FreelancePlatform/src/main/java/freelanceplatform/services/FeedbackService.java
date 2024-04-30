package freelanceplatform.services;

import freelanceplatform.data.FeedbackRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional
    public Feedback save(Feedback feedback) {
        Optional.ofNullable(feedback.getReceiver()).ifPresent(
                maybeReceiver -> {
                    this.getUser(maybeReceiver.getId()).ifPresent(user -> user.addReceivedFeedback(feedback));
                }
        );
        Optional.ofNullable(feedback.getSender()).ifPresent(
                maybeSender -> {
                    this.getUser(maybeSender.getId()).ifPresent(user -> user.addSentFeedback(feedback));
                }
        );
        return feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public Optional<Feedback> findById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

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

    @Transactional(readOnly = true)
    public List<Feedback> findByReceiver(User receiver) {
        return feedbackRepository.findByReceiver(receiver);
    }

    @Transactional(readOnly = true)
    public List<Feedback> findBySender(User sender) {
        return feedbackRepository.findBySender(sender);
    }

    @Transactional(readOnly = true)
    protected Optional<User> getUser(Integer id) {
        return Optional.ofNullable(id).flatMap(userRepository::findById);
    }
}
