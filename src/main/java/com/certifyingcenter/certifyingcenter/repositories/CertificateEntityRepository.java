package com.certifyingcenter.certifyingcenter.repositories;

import com.certifyingcenter.certifyingcenter.entryies.CertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateEntityRepository extends JpaRepository<CertificateEntity, Long> {
    public List<CertificateEntity> findAllByUsername(String username);
    public List<CertificateEntity> findAllByVerified(int verified);
    public CertificateEntity findByCertificateId(long certificateId);
}

