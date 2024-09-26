package com.sky.users.project.to;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record CreateExternalProjectTO(@NotEmpty String name) implements Serializable {
}
