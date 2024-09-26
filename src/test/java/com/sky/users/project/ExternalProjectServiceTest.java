package com.sky.users.project;

import com.sky.users.project.to.CreateExternalProjectTO;
import com.sky.users.project.to.ExternalProjectTO;
import com.sky.users.user.User;
import com.sky.users.user.UserDoesNotExistException;
import com.sky.users.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ExternalProjectServiceTest {

    private static final Long USER_ID = 1L;
    @Mock
    private ExternalProjectRepository externalProjectRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ExternalProjectService externalProjectService;
    private CreateExternalProjectTO createExternalProjectTO;
    private ExternalProject externalProject1;
    private ExternalProject externalProject2;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        externalProject1 = new ExternalProject();
        externalProject1.setId(1L);
        externalProject1.setName("Project One");

        externalProject2 = new ExternalProject();
        externalProject2.setId(2L);
        externalProject2.setName("Project Two");

        user = new User();
        user.setId(USER_ID);
        user.setPassword("pwd");
        user.setName("User Name");
        user.setEmail("email@email.com");

        createExternalProjectTO = new CreateExternalProjectTO("Project One");

    }

    @Test
    void testFindUserExternalProjectsSuccess() {
        List<ExternalProject> projectList = List.of(externalProject1, externalProject2);
        given(externalProjectRepository.findExternalProjectByUserId(USER_ID)).willReturn(projectList);
        List<ExternalProjectTO> externalProjects = externalProjectService.findUserExternalProjects(USER_ID);

        assertNotNull(externalProjects);
        assertEquals(2, externalProjects.size());

        assertEquals("Project One", externalProjects.get(0).name());
        assertEquals(1L, externalProjects.get(0).id());
        assertEquals("Project Two", externalProjects.get(1).name());
        assertEquals(2L, externalProjects.get(1).id());

        verify(externalProjectRepository, times(1)).findExternalProjectByUserId(USER_ID);
    }

    @Test
    void testFindUserExternalProjectsEmptyResult() {
        given(externalProjectRepository.findExternalProjectByUserId(USER_ID)).willReturn(new ArrayList<>());
        List<ExternalProjectTO> externalProjects = externalProjectService.findUserExternalProjects(USER_ID);

        assertNotNull(externalProjects);
        assertTrue(externalProjects.isEmpty());

        verify(externalProjectRepository, times(1)).findExternalProjectByUserId(USER_ID);
    }


    @Test
    public void createExternalProjectWhenUserExists() {
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(externalProject1);

        ExternalProjectTO createdProject = externalProjectService.createExternalProject(USER_ID, createExternalProjectTO);

        assertNotNull(createdProject);
        assertEquals(externalProject1.getId(), createdProject.id());
        assertEquals(externalProject1.getName(), createdProject.name());
    }

    @Test
    public void createExternalProjectWhenUserNotFound() {
        when(userService.getUserById(USER_ID)).thenThrow(new UserDoesNotExistException("User not found"));

        assertThrows(UserDoesNotExistException.class, () -> externalProjectService.createExternalProject(USER_ID, createExternalProjectTO));

        verify(externalProjectRepository, never()).save(any(ExternalProject.class));
    }


}
