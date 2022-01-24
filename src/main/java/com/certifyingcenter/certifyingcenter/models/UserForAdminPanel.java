package com.certifyingcenter.certifyingcenter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForAdminPanel {
    private long id;
    private String username;
    private String fistName;
    private String lastName;
    private boolean admin;


}
