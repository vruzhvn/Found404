package ru.javabegin.backend.found404.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
