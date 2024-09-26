package com.sky.users.project.to;

import java.io.Serializable;

public record ExternalProjectTO(Long id, String name) implements Serializable {
}