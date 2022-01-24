package com.certifyingcenter.certifyingcenter.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AjaxChangeRoleModel {
    private long userId;
    private String userName;
    @JsonProperty
    private boolean isAdmin;
}

