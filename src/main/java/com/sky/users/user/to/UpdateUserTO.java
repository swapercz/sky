package com.sky.users.user.to;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserTO(String password, @NotEmpty String name) {
}
