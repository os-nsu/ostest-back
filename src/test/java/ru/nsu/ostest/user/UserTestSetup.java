package ru.nsu.ostest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nsu.ostest.adapter.in.rest.model.user.*;
import ru.nsu.ostest.adapter.mapper.UserMapper;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.exception.UserNotFoundException;
import ru.nsu.ostest.domain.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@AutoConfigureMockMvc(addFilters = false)
public class UserTestSetup {

    private static final String PATH = "/api/user";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    public UserDto createUserReturnsUserDto(UserCreationRequestDto creationRequestDto) throws Exception {
        var user = createUserReturnsUserPasswordDto(creationRequestDto);

        assertTrue(userRepository.findByUsername(user.username()).isPresent());
        User userFromRepository = userRepository.findByUsername(user.username()).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.userToUserDto(userFromRepository);
    }

    public UserPasswordDto createUserReturnsUserPasswordDto(UserCreationRequestDto creationRequestDto) throws Exception {
        var result = mockMvc.perform(post("/api/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), UserPasswordDto.class);
    }

    public void createUserBad(UserCreationRequestDto creationRequestDto) throws Exception {

        mockMvc.perform(post("/api/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationRequestDto)))
                .andExpect(status().isBadRequest());

        assertEquals(1, userRepository.findAll().size());
    }

    public void deleteUser(Long userToDeleteId) throws Exception {
        mockMvc.perform(delete(PATH + "/{id}", userToDeleteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToDeleteId)))
                .andExpect(status().isOk());

        assertFalse(userRepository.findById(userToDeleteId).isPresent());
    }

    public UserDto getCurrentUser() throws Exception {
        var result = mockMvc.perform(get(PATH + "/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

    }

    public UserDto editUser(UserEditionRequestDto userEditionRequestDto, Long id) throws Exception {

        var result = mockMvc.perform(patch(PATH + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEditionRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        var user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertTrue(userRepository.findById(user.id()).isPresent());

        return user;
    }

    public void editUserBad(UserEditionRequestDto userEditionRequestDto, Long id) throws Exception {
        mockMvc.perform(patch(PATH + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEditionRequestDto)))
                .andExpect(status().isBadRequest());
    }

    public void changeUserPassword(ChangePasswordDto passwordDto, Principal mockPrincipal) throws Exception {
        mockMvc.perform(put(PATH + "/change-password")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());
    }

    public void changeUserPassword(ChangePasswordDto passwordDto, Long id) throws Exception {
        mockMvc.perform(put(PATH + "/change-password/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());
    }

    public UserDto getUserDto(String path) throws IOException {
        return objectMapper.readValue(
                Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8), UserDto.class
        );
    }

    public void loginUser(UserPasswordDto userPasswordDto) throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPasswordDto)))
                .andExpect(status().isOk());
    }

    public void loginUserBadRequest(UserPasswordDto userPasswordDto) throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPasswordDto)))
                .andExpect(status().isUnauthorized());
    }
}
