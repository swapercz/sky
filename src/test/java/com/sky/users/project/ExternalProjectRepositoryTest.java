package com.sky.users.project;


import com.sky.users.user.User;
import com.sky.users.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class ExternalProjectRepositoryTest {

    @Autowired
    private ExternalProjectRepository externalProjectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("email@email.com");
        user.setName("User name");
        user.setPassword("pwd");
        user = userRepository.save(user);
    }

    @Test
    void queryProjectForExisingUser() {
        ExternalProject externalProject = new ExternalProject();
        externalProject.setUser(user);
        externalProject.setName("Project Name");
        externalProject = externalProjectRepository.save(externalProject);

        List<ExternalProject> selectedProjects = externalProjectRepository.findExternalProjectByUserId(user.getId());
        assertEquals(1, selectedProjects.size());
        ExternalProject selectedProject = selectedProjects.getFirst();
        assertEquals(externalProject.getName(), selectedProject.getName());
        assertEquals(externalProject.getId(), selectedProject.getId());

    }

    @Test
    void queryProjectForNonExistingUser() {
        List<ExternalProject> selectedProjects = externalProjectRepository.findExternalProjectByUserId(1234L);
        assertTrue(selectedProjects.isEmpty());
    }


}