package com.petgram.auth_service.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String password;
}
