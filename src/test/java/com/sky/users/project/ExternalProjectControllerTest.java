package com.sky.users.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.users.project.to.CreateExternalProjectTO;
import com.sky.users.project.to.ExternalProjectTO;
import com.sky.users.user.UserDoesNotExistException;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ExternalProjectController.class)
@AutoConfigureDataJpa
public class ExternalProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalProjectService externalProjectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExternalProjectTO projectTO;
    private CreateExternalProjectTO createExternalProjectTO;

    @BeforeEach
    void setUp() {
        projectTO = new ExternalProjectTO(1L, "Project One");
        createExternalProjectTO = new CreateExternalProjectTO("New Project");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetExternalProjects() throws Exception {
        List<ExternalProjectTO> projectList = List.of(projectTO);
        given(externalProjectService.findUserExternalProjects(1L)).willReturn(projectList);

        mockMvc.perform(get("/api/users/1/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Project One"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddExternalProject() throws Exception {
        given(externalProjectService.createExternalProject(eq(1L), any(CreateExternalProjectTO.class)))
                .willReturn(projectTO);

        mockMvc.perform(post("/api/users/1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(createExternalProjectTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/user/1/projects/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Project One"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddExternalProjectForInvalidUser() throws Exception {
        given(externalProjectService.createExternalProject(eq(1L), any(CreateExternalProjectTO.class)))
                .willThrow(new UserDoesNotExistException("User not found"));

        mockMvc.perform(post("/api/users/1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(createExternalProjectTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddExternalProjectWithDuplicity() throws Exception {
        given(externalProjectService.createExternalProject(eq(1L), any(CreateExternalProjectTO.class)))
                .willThrow(new DataIntegrityViolationException("Duplicity key"));

        mockMvc.perform(post("/api/users/1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(createExternalProjectTO)))
                .andExpect(status().isConflict());
    }

}