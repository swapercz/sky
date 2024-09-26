package com.sky.users.project;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExternalProjectRepository extends CrudRepository<ExternalProject, Long> {

    List<ExternalProject> findExternalProjectByUserId(Long userId);
}
