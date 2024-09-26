package com.sky.users.project;

import com.sky.users.project.to.CreateExternalProjectTO;
import com.sky.users.project.to.ExternalProjectTO;
import com.sky.users.user.UserService;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.sky.users.config.CacheConfig.USER_CACHE;

@Service
public class ExternalProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalProjectService.class);
    private final ExternalProjectRepository externalProjectRepository;
    private final UserService userService;

    public ExternalProjectService(ExternalProjectRepository externalProjectRepository, UserService userService) {
        this.externalProjectRepository = externalProjectRepository;
        this.userService = userService;
    }

    public List<ExternalProjectTO> findUserExternalProjects(final Long userId) {
        return externalProjectRepository.findExternalProjectByUserId(userId).stream()
                .map(ExternalProjectMapper.INSTANCE::toProjectTO).collect(Collectors.toList());
    }

    @Observed(name = "project.created", contextualName = "createExternalProject")
    @CacheEvict(value = USER_CACHE, key = "#a0")
    public ExternalProjectTO createExternalProject(Long userId, CreateExternalProjectTO createExternalProjectTO) {
        var selectedUser = userService.getUserById(userId);
        ExternalProject externalProject = new ExternalProject();
        externalProject.setUser(selectedUser);
        externalProject.setName(createExternalProjectTO.name());
        var createdProject = externalProjectRepository.save(externalProject);
        logger.info("New project '{}' has been registered for user '{}'", createdProject.getName(), userId);
        return ExternalProjectMapper.INSTANCE.toProjectTO(createdProject);
    }
}
