package com.certifyingcenter.certifyingcenter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPageCertInfo {
    private long certId;
    private int verified;
    private String certSubmitTime;
    private String certSubmitDate;
    private String status;
}

