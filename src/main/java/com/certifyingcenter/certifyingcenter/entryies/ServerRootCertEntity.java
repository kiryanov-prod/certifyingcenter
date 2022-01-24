package com.certifyingcenter.certifyingcenter.entryies;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server_root_cert_table")
@Getter
@Setter
@EqualsAndHashCode
public class ServerRootCertEntity {

    @Id
    @Column(name = "cert_name")
    private String certName;

    @Column(name = "cert_bytes")
    private byte [] certByteArray;

    @Column(name = "cert_private_key_bytes")
    private byte [] certPrivateKeyByteArray;

}
