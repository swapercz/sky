package com.sky.users.project;

import com.sky.users.project.to.ExternalProjectTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExternalProjectMapper {
    ExternalProjectMapper INSTANCE = Mappers.getMapper(ExternalProjectMapper.class);

    ExternalProjectTO toProjectTO(ExternalProject externalProject);
}
