package freelanceplatform.controllers;

import freelanceplatform.model.Feedback;
import freelanceplatform.services.FeedbackService;
import freelanceplatform.utils.IntegrationTestBase;
import freelanceplatform.utils.httpAuths.WithAuthentificatedAdmin;
import freelanceplatform.utils.httpAuths.WithAuthentificatedUser;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class FeedbackControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final FeedbackService feedbackService;

    @Autowired
    FeedbackControllerTest(MockMvc mockMvc, FeedbackService feedbackService) {
        this.mockMvc = mockMvc;
        this.feedbackService = feedbackService;
    }

//    void init() {
////        Sender sender
//        Feedback feedback = new Feedback();
//        feedback.setSender();
//    }

//    @Test
//    @WithAuthentificatedUser
//    void findByIdReturnsStatusOk() throws Exception {
//        mockMvc.perform(get("rest/feedbacks/1"))
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
//                .andExpect(MockMvcResultMatchers.model().attribute("id", 1));
//    }
//    @Test
//    @WithAuthentificatedUser
//    void findByIdReturnsStatusNotFound() throws Exception {
//        mockMvc.perform(get("rest/feedbacks/-1"))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
//    @Test
//    @WithAuthentificatedUser
//    void findAllReturnsStatusOk() throws Exception {
//        mockMvc.perform(get("rest/feedbacks"))
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
//    }

    @Test
    void findAllReturnsStatusOk() throws Exception {
        mockMvc.perform(get("/rest/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(feedbackService.findAll().size())));
    }

//    @Test
//    @WithAuthentificatedAdmin
//    void updateByAdminReturnsStatusNoContent() throws Exception {
//
//        mockMvc.perform(put("rest/feedbacks/1")
//
////                .andExpect(MockMvcResultMatchers.status().isForbidden());
//    }

//    @Test
//    @WithAuthentificatedUser
//    void updateByUserReturnsStatusForbidden() {
//    }
//
//    @Test
//    @WithAuthentificatedUser
//    void updateByUserReturnsStatusNoContent() {
//    }
//
//    @Test
//    @WithAuthentificatedUser
//    void saveByUserReturnsStatusForbidden() {
//    }
//
//    @Test
//    @WithAuthentificatedUser
//    void saveByUserReturnsStatusCreated() {
//    }
//
//    @Test
//    @WithAuthentificatedAdmin
//    void saveByAdminReturnsStatusCreated() {
//    }
//
//    @Test
//    @WithAuthentificatedAdmin
//    void deleteByAdminReturnsStatusNoContent() {
//    }
//
//    @Test
//    @WithAuthentificatedUser
//    void deleteByUserReturnsStatusForbidden() {
//    }
}