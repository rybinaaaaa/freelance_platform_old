package freelanceplatform.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.dto.entityDTO.TaskDTO;
import freelanceplatform.environment.Generator;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.*;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import freelanceplatform.utils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.h2.value.ValueToObjectConverter.readValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;


import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class TaskControllerTest extends IntegrationTestBase {


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TaskService taskService;
    private final UserService userService;

    private User userAdmin;
    private User emptyUser;

    private final CacheManager cacheManager;


    @Autowired
    public TaskControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, TaskService taskService, UserService userService, CacheManager cacheManager) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.taskService = taskService;
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    @BeforeEach
    public void init() {
        clearAllCaches();
        userAdmin = Generator.generateUser();
        userAdmin.setRole(Role.ADMIN);
        userService.save(userAdmin);

        emptyUser = Generator.generateUser();
        emptyUser.setRole(Role.USER);
        userService.save(emptyUser);
    }

    private void clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    public void saveByUserReturnsStatusCreated() throws Exception {
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveByAdminReturnsStatusCreated() throws Exception {
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);
        emptyUser.setRole(Role.ADMIN);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveByGuestReturnsStatusForbidden() throws Exception{
        Task task = Generator.generateTask();
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);
        emptyUser.setRole(Role.GUEST);

        mockMvc.perform(post("/rest/tasks")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getByIdReturnsStatusOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    public void getByIdReturnsNotFoundForUnknownId() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTaskBoardReturnsTasksWithStatusUnassignedAndStatusOk() throws Exception{
        final List<Task> taskBoardTasks = IntStream.range(0, 5).mapToObj(i -> {
            final Task task = Generator.generateTask();
            task.setStatus(TaskStatus.UNASSIGNED);
            task.setCustomer(emptyUser);
            return task;
        }).toList();
        final List<Task> otherTasks = IntStream.range(0, 5).mapToObj(i -> {
            final Task task = Generator.generateTask();
            task.setStatus(TaskStatus.SUBMITTED);
            task.setCustomer(emptyUser);
            return task;
        }).toList();

        taskService.saveAll(taskBoardTasks);
        taskService.saveAll(otherTasks);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks/taskBoard")
                .param("fromNewest", "true")
                .param("type", ""))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final Iterable<TaskDTO> result = objectMapper.readValue(jsonContent, new TypeReference<>() {});
        final List<TaskDTO> list = StreamSupport.stream(result.spliterator(), false)
                .toList();

        assertEquals(list.size(), taskBoardTasks.size());
        list.forEach(taskDTO -> assertEquals(taskDTO.getStatus(), TaskStatus.UNASSIGNED));
    }

    @Test
    public void getAllTakenReturnsTasksWithMatchingFreelancerUsernameAndStatusOk() throws Exception{
        final List<Task> taken = IntStream.range(0, 5).mapToObj(i -> {
            final Task task = Generator.generateTask();
            task.setStatus(TaskStatus.UNASSIGNED);
            task.setCustomer(userAdmin);
            task.setFreelancer(emptyUser);
            return task;
        }).toList();
        taskService.saveAll(taken);
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks/taken")
                        .with(user(new UserDetails(emptyUser)))
                        .param("expired", "false"))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final Iterable<TaskDTO> result = objectMapper.readValue(jsonContent, new TypeReference<>() {});
        final List<TaskDTO> list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(list.size(), taken.size());
        list.forEach(taskDTO -> assertEquals(taskDTO.getFreelancerUsername(), emptyUser.getUsername()));
    }

    @Test
    public void getAllPostedReturnsTasksWithMatchingCustomerUsernameAndStatusOk() throws Exception{
        final List<Task> posted = IntStream.range(0, 5).mapToObj(i -> {
            final Task task = Generator.generateTask();
            task.setStatus(TaskStatus.UNASSIGNED);
            task.setCustomer(emptyUser);
            return task;
        }).toList();
        taskService.saveAll(posted);
        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks/posted")
                        .with(user(new UserDetails(emptyUser)))
                        .param("expired", "false"))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final Iterable<TaskDTO> result = objectMapper.readValue(jsonContent, new TypeReference<>() {});
        final List<TaskDTO> list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(list.size(), posted.size());
        list.forEach(taskDTO -> assertEquals(taskDTO.getCustomerUsername(), emptyUser.getUsername()));
    }

    @Test
    public void updateReturnsNotFoundForWrongId() throws Exception{
        Task task = taskService.getById(1);
        task.setFreelancer(userAdmin);
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(put("/rest/tasks/posted/-1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateReturnsBadRequestIfTaskStatusIsNotUnassigned() throws Exception{
        Task task = taskService.getById(1);
        task.setStatus(TaskStatus.ASSIGNED);
        taskService.save(task);
        task.setTitle("New title");
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(put("/rest/tasks/posted/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateByUserReturnsStatusNoContent() throws Exception{
        Task task = taskService.getById(1);
        task.setStatus(TaskStatus.UNASSIGNED);
        taskService.save(task);
        task.setTitle("New title");
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(put("/rest/tasks/posted/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateByAdminReturnsStatusForbidden() throws Exception{
        Task task = taskService.getById(1);
        task.setStatus(TaskStatus.UNASSIGNED);
        taskService.save(task);
        task.setTitle("New title");
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(put("/rest/tasks/posted/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        Task task = taskService.getById(1);
        task.setStatus(TaskStatus.UNASSIGNED);
        taskService.save(task);
        task.setTitle("New title");
        TaskCreationDTO taskDTO= new TaskCreationDTO(task.getCustomer(), task.getTitle(), task.getProblem(), task.getDeadline(), task.getStatus(), task.getPayment(), task.getType());
        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(put("/rest/tasks/posted/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(taskJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteReturnsNotFoundForWrongId() throws Exception {
        mockMvc.perform(delete("/rest/tasks/posted/-1")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteByAdminReturnsStatusNoContent() throws Exception{
        Task task = Generator.generateTask();
        task.setCustomer(userAdmin);
        taskService.save(task);

        mockMvc.perform(delete("/rest/tasks/posted/"+task.getId())
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteByUserReturnsStatusNoContent() throws Exception{
        mockMvc.perform(delete("/rest/tasks/posted/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(delete("/rest/tasks/posted/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void assignFreelancerByUserReturnsStatusNoContent() throws Exception {
        mockMvc.perform(post("/rest/tasks/posted/1/proposals/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void assignFreelancerByAdminReturnsStatusForbidden() throws Exception{
        mockMvc.perform(post("/rest/tasks/posted/1/proposals/1")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void assignFreelancerByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(post("/rest/tasks/posted/1/proposals/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void acceptByUserReturnsStatusNoContent() throws Exception{
        mockMvc.perform(post("/rest/tasks/posted/1/accept")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void acceptByAdminReturnsStatusForbidden() throws Exception{
        mockMvc.perform(post("/rest/tasks/posted/1/accept")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void acceptByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(post("/rest/tasks/posted/1/accept")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void removeFreelancerByUserReturnsStatusNoContent() throws Exception{
        mockMvc.perform(post("/rest/tasks/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void removeFreelancerByAdminReturnsStatusForbidden() throws Exception{
        mockMvc.perform(post("/rest/tasks/1")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void removeFreelancerByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(post("/rest/tasks/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void attachSolutionByUserReturnsStatusNoContent() throws Exception{
        final Solution solution = Generator.generateSolution();
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/tasks/taken/1/attach-solution")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void attachSolutionByAdminReturnsStatusForbidden() throws Exception{
        final Solution solution = Generator.generateSolution();
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/tasks/taken/1/attach-solution")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void attachSolutionByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        final Solution solution = Generator.generateSolution();
        String solutionJson = objectMapper.writeValueAsString(solution);

        mockMvc.perform(post("/rest/tasks/taken/1/attach-solution")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(solutionJson).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void sendOnReviewByUserReturnsStatusNoContent() throws Exception{
        mockMvc.perform(post("/rest/tasks/taken/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void sendOnReviewByAdminReturnsStatusForbidden() throws Exception{
        mockMvc.perform(post("/rest/tasks/taken/1")
                        .with(user(new UserDetails(userAdmin))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void sendOnReviewByGuestReturnsStatusForbidden() throws Exception{
        emptyUser.setRole(Role.GUEST);
        mockMvc.perform(post("/rest/tasks/taken/1")
                        .with(user(new UserDetails(emptyUser))))
                .andExpect(status().isForbidden());
    }
}
