package freelanceplatform.services;

import freelanceplatform.data.FeedbackRepository;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public Optional<Feedback> getFeedbackById(Long id) {
        return feedbackRepository.findById(id);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

//    @Transactional
    public void deleteFeedbackById(Long id) {
        feedbackRepository.deleteById(id);
//        feedbackRepository.findById(id)
//                .ifPresent(feedback -> {
//                    User sender = feedback.getSender();
//                    User receiver = feedback.getReceiver();
//
//                    sender.deleteSentFeedback(feedback);
//                    receiver.deleteReceivedFeedback(feedback);
//
//                    feedback.setSender(null);
//                    feedback.setReceiver(null);
//
//                    feedbackRepository.delete(feedback);
//                });
    }

    public List<Feedback> getFeedbacksByReceiver(User receiver) {
        return feedbackRepository.findByReceiver(receiver);
    }

    public List<Feedback> getFeedbacksBySender(User sender) {
        return feedbackRepository.findBySender(sender);
    }
}
