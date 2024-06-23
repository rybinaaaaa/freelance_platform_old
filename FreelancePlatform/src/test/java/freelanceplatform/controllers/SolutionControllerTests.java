package freelanceplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.environment.Generator;
import freelanceplatform.model.Role;
import freelanceplatform.model.Solution;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.SolutionService;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import freelanceplatform.utils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SolutionControllerTests extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SolutionService solutionService;
    private final UserService userService;
    private final TaskService taskService;

    private User userAdmin;
    private User emptyUser;
    private Task task;

    @Autowired
    public SolutionControllerTests(MockMvc mockMvc, ObjectMapper objectMapper, SolutionService solutionService, UserService userService, TaskService taskService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.solutionService = solutionService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @BeforeEach
    public void init() {
        userAdmin = Generator.generateUser();
        userAdmin.setRole(Role.ADMIN);
        userService.save(userAdmin);

        emptyUser = Generator.generateUser();
        emptyUser.setRole(Role.USER);
        userService.save(emptyUser);

        task = Generator.generateTask();
        task.setCustomer(emptyUser);
        taskService.save(task);
    }

    @Test
    public void saveByUserReturnsStatusCreated() throws Exception{
        Solution solution = Generator.generateSolution();
        solution.setTask(taskService.getById(1));
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/solutions")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveByAdminReturnsStatusForbidden() throws Exception{
        Solution solution = Generator.generateSolution();
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/solutions")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void saveByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        Solution solution = Generator.generateSolution();
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/solutions")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getByIdReturnsStatusOk() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/solutions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    public void getByIdReturnsNotFoundForUnknownId() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/solutions/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateReturnsNotFoundForWrongId() throws Exception{
        Solution solution = solutionService.getById(1);
        solution.setDescription("new description");
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(put("/rest/solutions/-1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateByUserReturnsStatusNoContent() throws Exception{
        Solution solution = solutionService.getById(1);
        solution.setDescription("new description");
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(put("/rest/solutions/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateByAdminReturnsStatusForbidden() throws Exception{
        Solution solution = solutionService.getById(1);
        solution.setDescription("new description");
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(put("/rest/solutions/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        Solution solution = solutionService.getById(1);
        solution.setDescription("new description");
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(put("/rest/solutions/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteReturnsNotFoundForWrongId() throws Exception{
        mockMvc.perform(delete("/rest/solutions/-1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteByAdminReturnsStatusForbidden() throws Exception{
        mockMvc.perform(delete("/rest/solutions/1")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(delete("/rest/solutions/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteByUserReturnsStatusNoContent() throws Exception{
        Solution solution = solutionService.getById(1);
        solution.setTask(task);
        solutionService.save(solution);
        mockMvc.perform(delete("/rest/solutions/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }
}
