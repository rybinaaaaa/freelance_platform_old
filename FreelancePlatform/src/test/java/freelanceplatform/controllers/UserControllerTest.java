package freelanceplatform.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.UserDTO;
import freelanceplatform.environment.Generator;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends BaseControllerTest{
    @Mock
    private UserService userServiceMock;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UserController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void getAllGetsAllUsersByUsingUserService() throws Exception {
        final List<User> users = new ArrayList<>();
        for (int i = 0; i<5; ++i){
            User user = Generator.generateUser();
            user.setId(i);
            users.add(user);
        }
        when(userServiceMock.findAll()).thenReturn(users);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/users")).andReturn();
        final List<UserDTO> result = readValue(mvcResult, new TypeReference<>() {
        });
        assertNotNull(result);
        assertEquals(result.size(), users.size());
        verify(userServiceMock).findAll();
    }

    @Test
    public void registerSavesByUsingUserService() throws Exception {
        final User user = Generator.generateUser();
        UserCreationDTO userCreationDTO = new UserCreationDTO(user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPassword());
        User savedUser = mapper.userDTOToUser(userCreationDTO);
        mockMvc.perform(post("/rest/users")
                        .content(toJson(userCreationDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(userServiceMock).save(savedUser);
    }

    @Test
    public void updateUserUpdatesByUsingUserService() throws Exception{
        final User currentUser = Generator.generateUser();
        currentUser.setUsername("Stas");
        currentUser.setId(1337);

        Authentication authMock = mock(Authentication.class);
        UserDetails userDetailsMock = mock(UserDetails.class);

        when(authMock.getPrincipal()).thenReturn(userDetailsMock);
        when(userDetailsMock.getUser()).thenReturn(currentUser);
        when(userServiceMock.find(1337)).thenReturn(currentUser);

        final UserDTO updatedUser = new UserDTO(currentUser.getId(), "Stasiks", currentUser.getFirstName(),
                currentUser.getLastName(), currentUser.getEmail(), currentUser.getRating(), currentUser.getRole());

        mockMvc.perform(put("/rest/users/" + currentUser.getId())
                        .content(toJson(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authMock))
                .andExpect(status().isOk());
        verify(userServiceMock).update(any(User.class));
    }
}
