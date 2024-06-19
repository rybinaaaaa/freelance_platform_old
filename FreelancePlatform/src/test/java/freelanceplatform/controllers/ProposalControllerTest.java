package freelanceplatform.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.Mapper;
import freelanceplatform.environment.Generator;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Role;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.ProposalService;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import freelanceplatform.utils.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProposalControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ProposalService proposalService;
    private final ObjectMapper objectMapper;
    private final Mapper mapper;
    private final UserService userService;
    private final TaskService taskService;

    private User userAdmin;
    private User emptyUser;

    @Autowired
    public ProposalControllerTest(MockMvc mockMvc, ProposalService proposalService, ObjectMapper objectMapper, Mapper mapper, UserService userService, TaskService taskService) {
        this.mockMvc = mockMvc;
        this.proposalService = proposalService;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.userService = userService;
        this.taskService = taskService;
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
        mockMvc.perform(get("/rest/proposals/1"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    void findByIdReturnsStatusNotFound() throws Exception {
        mockMvc.perform(get("/rest/proposals/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findAllReturnsStatusOk() throws Exception {
        mockMvc.perform(get("/rest/proposals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(proposalService.findAll().size())));
    }

    @Test
    void updateByAdminReturnsStatusNoContent() throws Exception {
        Proposal proposal = proposalService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        proposal.setFreelancer(emptyUser);


        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(put("/rest/proposals/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateByUserReturnsStatusForbidden() throws Exception {
        Proposal proposal = proposalService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        proposal.setFreelancer(userAdmin);


        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(put("/rest/proposals/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateByUserReturnsStatusNoContent() throws Exception {
        Proposal proposal = proposalService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));
        proposal.setFreelancer(emptyUser);


        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(put("/rest/proposals/1")
                        .with(user(new UserDetails(proposal.getFreelancer())))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveByUserReturnsStatusForbidden() throws Exception {
        Proposal proposal = Generator.generateProposal();
        userService.save(proposal.getFreelancer());

        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(post("/rest/proposals")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveByUserReturnsStatusCreated() throws Exception {
        Proposal proposal = Generator.generateProposal();
        userService.save(proposal.getFreelancer());
        taskService.save(proposal.getTask());

        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(post("/rest/proposals")
                        .with(user(new UserDetails(proposal.getFreelancer())))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveByAdminReturnsStatusCreated() throws Exception {
        Proposal proposal = Generator.generateProposal();
        userService.save(proposal.getFreelancer());
        taskService.save(proposal.getTask());

        String pr = objectMapper.writeValueAsString(mapper.proposalToProposalDTO(proposal));

        mockMvc.perform(post("/rest/proposals")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(pr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteByAdminReturnsStatusNoContent() throws Exception {
        Proposal proposal = proposalService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));

        mockMvc.perform(delete("/rest/proposals/1")
                        .with(user(new UserDetails(userAdmin)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteByUserReturnsStatusForbidden() throws Exception {
        Proposal proposal = proposalService.findById(1).orElseThrow(() -> new NotFoundException("Incorrect id in tests!"));

        mockMvc.perform(delete("/rest/proposals/1")
                        .with(user(new UserDetails(emptyUser)))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isForbidden());
    }
}
