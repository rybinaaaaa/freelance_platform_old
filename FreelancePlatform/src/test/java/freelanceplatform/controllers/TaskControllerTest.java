package freelanceplatform.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.environment.Generator;
import freelanceplatform.model.Role;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import freelanceplatform.utils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.hazelcast.jet.json.JsonUtil.toJson;
import static org.h2.value.ValueToObjectConverter.readValue;
import static org.hibernate.Hibernate.get;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;


public class TaskControllerTest extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Mapper mapper;
    private final TaskService taskService;
    private final UserService userService;

    private User userAdmin;
    private User emptyUser;


    @Autowired
    public TaskControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, Mapper mapper, TaskService taskService, UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.taskService = taskService;
        this.userService = userService;
    }

    @BeforeEach
    public void init() {
        userAdmin = Generator.generateUser();
        userAdmin.setRole(Role.ADMIN);
        userService.save(userAdmin);

        emptyUser = Generator.generateUser();
        emptyUser.setRole(Role.USER);
        userService.save(emptyUser);
    }

    @Test
    public void saveByUserReturnsStatusCreated() throws Exception {
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String pr = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveByAdminReturnsStatusCreated() throws Exception {
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String pr = objectMapper.writeValueAsString(taskDTO);
        emptyUser.setRole(Role.ADMIN);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveByGuestReturnsStatusForbidden() throws Exception{
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String pr = objectMapper.writeValueAsString(taskDTO);
        emptyUser.setRole(Role.GUEST);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getByIdReturnsStatusOk() throws Exception {

    }

    @Test
    public void getByIdReturnsNotFoundForUnknownId() throws Exception{}

    @Test
    public void getAllTaskBoardReturnsTasksWithStatusUnassignedAndStatusOk() throws Exception{

    }

    @Test
    public void getAllTakenReturnsTasksWithMatchingFreelancerIdAndStatusOk() throws Exception{}

    @Test
    public void getAllPostedReturnsTasksWithMatchingCustomerIdAndStatusOk() throws Exception{}

    @Test
    public void updateReturnsNotFoundForWrongId() throws Exception{}

    @Test
    public void updateReturnsBadRequestIfTaskStatusIsNotUnassigned() throws Exception{}

    @Test
    public void updateByUserReturnsStatusNoContent() throws Exception{}

    @Test
    public void updateByAdminReturnsStatusForbidden() throws Exception{}

    @Test
    public void deleteReturnsNotFoundForWrongId(){}

    @Test
    public void deleteByAdminReturnsStatusNoContent() throws Exception{}

    @Test
    public void deleteByUserReturnsStatusNoContent() throws Exception{}

    @Test
    public void deleteByGuestReturnsStatusForbidden() throws Exception{}

    @Test
    public void assignFreelancerByUserReturnsStatusOk(){}

    @Test
    public void assignFreelancerByAdminReturnsStatusForbidden(){}

    @Test
    public void assignFreelancerByGuestReturnsStatusForbidden(){}

    @Test
    public void acceptByUserReturnsStatusOk(){}

    @Test
    public void acceptByAdminReturnsStatusForbidden(){}

    @Test
    public void acceptByGuestReturnsStatusForbidden(){}

    @Test
    public void removeFreelancerByUserReturnsStatusNoContent(){}

    @Test
    public void removeFreelancerByAdminReturnsStatusForbidden(){}

    @Test
    public void removeFreelancerByGuestReturnsStatusForbidden(){}

    @Test
    public void attachSolutionByUserReturnsStatusNoContent(){}

    @Test
    public void attachSolutionByAdminReturnsStatusForbidden(){}

    @Test
    public void attachSolutionByGuestReturnsStatusForbidden(){}

    @Test
    public void sendOnReviewByUserReturnsStatusNoContent(){}

    @Test
    public void sendOnReviewByAdminReturnsStatusForbidden(){}

    @Test
    public void sendOnReviewByGuestReturnsStatusForbidden(){}

}
