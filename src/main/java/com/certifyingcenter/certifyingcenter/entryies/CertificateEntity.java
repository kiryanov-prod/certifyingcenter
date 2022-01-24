package com.certifyingcenter.certifyingcenter.entryies;


import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "certificate_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CertificateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private long certificateId;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_cert")
    private String emailCert;

    @Column(name = "cert")
    private byte [] certificateByteArr;

    @Column(name = "verified")
    private int verified;

    //Дата подачи заявка на сертификат
    @Column(name = "time_of_application")
    private Date timeOfApplication;

}
