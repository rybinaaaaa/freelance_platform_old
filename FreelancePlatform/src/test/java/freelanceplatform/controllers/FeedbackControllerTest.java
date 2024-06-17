package freelanceplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.Mapper;
import freelanceplatform.environment.Generator;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.Role;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.FeedbackService;
import freelanceplatform.services.UserService;
import freelanceplatform.utils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class FeedbackControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final FeedbackService feedbackService;
    private final ObjectMapper objectMapper;
    private final Mapper mapper;
    private final UserService userService;

    private User userAdmin;
    private User emptyUser;

    @Autowired
    public FeedbackControllerTest(MockMvc mockMvc, FeedbackService feedbackService, ObjectMapper objectMapper, Mapper mapper, UserService userService) {
        this.mockMvc = mockMvc;
        this.feedbackService = feedbackService;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.userService = userService;
    }

    @BeforeEach
    void init() {
        userAdmin = Generator.generateUser();
        userAdmin.setRole(Role.ADMIN);
        userService.save(userAdmin);

        emptyUser = Generator.generateUser();
        emptyUser.setRole(Role.USER);
        userService.save(emptyUser);
    }

    @Test
    void findByIdReturnsStatusOk() throws Exception {
        mockMvc.perform(get("/rest/feedbacks/1"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    void findByIdReturnsStatusNotFound() throws Exception {
        mockMvc.perform(get("/rest/feedbacks/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findAllReturnsStatusOk() throws Exception {
        mockMvc.perform(get("/rest/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(feedbackService.findAll().size())));
    }

    @Test
    void updateByAdminReturnsStatusNoContent() throws Exception {
        Feedback feedback = feedbackService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        feedback.setComment("test");


        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(put("/rest/feedbacks/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateByUserReturnsStatusForbidden() throws Exception {
        Feedback feedback = feedbackService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        feedback.setComment("test");


        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(put("/rest/feedbacks/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateByUserReturnsStatusNoContent() throws Exception {
        Feedback feedback = feedbackService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        feedback.setComment("test");


        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(put("/rest/feedbacks/1")
                        .with(user(new UserDetails(feedback.getSender())))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveByUserReturnsStatusForbidden() throws Exception {
        Feedback feedback = Generator.generateFeedback();
        userService.save(feedback.getSender());
        userService.save(feedback.getReceiver());

        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(post("/rest/feedbacks")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveByUserReturnsStatusCreated() throws Exception {
        Feedback feedback = Generator.generateFeedback();
        userService.save(feedback.getSender());
        userService.save(feedback.getReceiver());

        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(post("/rest/feedbacks")
                        .with(user(new UserDetails(feedback.getSender())))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveByAdminReturnsStatusCreated() throws Exception {
        Feedback feedback = Generator.generateFeedback();
        userService.save(feedback.getSender());
        userService.save(feedback.getReceiver());

        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(post("/rest/feedbacks")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteByAdminReturnsStatusNoContent() throws Exception {
        Feedback feedback = feedbackService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));        feedback.setComment("test");

        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(delete("/rest/feedbacks/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteByUserReturnsStatusForbidden() throws Exception {
        Feedback feedback = feedbackService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));

        String fb = objectMapper.writeValueAsString(mapper.feedbackToFeedbackDto(feedback));

        mockMvc.perform(delete("/rest/feedbacks/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(fb).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}