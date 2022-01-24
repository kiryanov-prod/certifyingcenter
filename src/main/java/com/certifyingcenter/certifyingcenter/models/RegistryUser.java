package com.certifyingcenter.certifyingcenter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistryUser {
    private String firsName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;
}
