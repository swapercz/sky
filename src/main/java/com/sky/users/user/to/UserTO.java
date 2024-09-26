package com.sky.users.user.to;

import java.io.Serializable;

public record UserTO(Long id, String email, String password, String name) implements Serializable {
}
