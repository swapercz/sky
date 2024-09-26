package com.sky.users.common;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class Utils {

    public static URI composeUserPath(Long userId) {
        return UriComponentsBuilder.fromPath("/api/user/{id}").buildAndExpand(userId).toUri();
    }

    public static URI composeUserPath(Long userId, Long projectId) {
        return UriComponentsBuilder.fromPath("/api/user/{id}/projects/{projectId}").buildAndExpand(userId, projectId).toUri();

    }
}
