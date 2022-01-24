package com.certifyingcenter.certifyingcenter.models;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CertificateRequest {
    private String domainName;
    private String organization;
    //отдел
    private String department;
    private String city;

    private String region;
    private String county;
    private String email;
    private MultipartFile multipartFile;
}
