package com.certifyingcenter.certifyingcenter.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateInformation {
    private String nameAndLastName;
    //Время когда был отправлен запрос на сертификат
    private String timeOfApplication;
    //С какого числа сертификат активен
    private String timeOfNotBefore;
    //По какое число
    private String timeOfNotAfter;

    private String domainName;
    private String city;
    private String organization;
    private String department;
    private String region;
    private String country;
    private String email;
    private String cert;
}