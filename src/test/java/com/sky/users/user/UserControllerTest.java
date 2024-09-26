package com.sky.users.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.users.user.to.CreateUserTO;
import com.sky.users.user.to.UpdateUserTO;
import com.sky.users.user.to.UserTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
@AutoConfigureDataJpa
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserTO userTO;
    private CreateUserTO createUserTO;
    private UpdateUserTO updateUserTO;

    @BeforeEach
    void setUp() {

        userTO = new UserTO(1L, "test@example.com", "pwd", "John Doe");
        createUserTO = new CreateUserTO("newuser@example.com", "password", "New User");
        updateUserTO = new UpdateUserTO("Updated Password", "Updated Name");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetUserById() throws Exception {
        given(userService.findUserById(1L)).willReturn(Optional.of(userTO));

        mockMvc.perform(get("/api/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser("USER")
    void testGetUserByIdNotFound() throws Exception {
        given(userService.findUserById(1L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUserAsAdmin() throws Exception {
        given(userService.saveUser(any(CreateUserTO.class))).willReturn(userTO);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/api/user/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.password").value("pwd"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUserAsAdminWithInvalidEmail() throws Exception {
        createUserTO = new CreateUserTO("INVALID_EMAIL", "password", "New User");
        given(userService.saveUser(any(CreateUserTO.class))).willReturn(userTO);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUserAsAdminWithMissingRequiredInputs() throws Exception {
        createUserTO = new CreateUserTO(null, "password", "New User");
        given(userService.saveUser(any(CreateUserTO.class))).willReturn(userTO);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUserAsAdminWithWithDuplicity() throws Exception {
        given(userService.saveUser(any(CreateUserTO.class))).willThrow(new DataIntegrityViolationException("Already exists"));

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserAsAdmin() throws Exception {
        userTO = new UserTO(userTO.id(), userTO.email(), updateUserTO.password(), updateUserTO.name());
        given(userService.updateUser(eq(1L), any(UpdateUserTO.class)))
                .willReturn(Optional.of(userTO));

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.password").value("Updated Password"))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void testDeleteUserAsAdmin() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateUserForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateUserForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteUserForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}