package com.certifyingcenter.certifyingcenter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BriefInformationAboutTheCertificate {
    private long certId;
    private int verified;
    private String nameAndLatsName;
    private String date;
    private String time;
}
