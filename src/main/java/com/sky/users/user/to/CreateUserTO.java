package com.sky.users.user.to;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record CreateUserTO(@Email @NotNull String email, @NotNull String password,
                           @NotEmpty String name) implements Serializable {
}
