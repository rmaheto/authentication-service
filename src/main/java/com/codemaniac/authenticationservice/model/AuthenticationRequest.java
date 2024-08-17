package com.codemaniac.authenticationservice.model;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String logonId;
    private String password;
    private String audience;
    private Role role;
}
