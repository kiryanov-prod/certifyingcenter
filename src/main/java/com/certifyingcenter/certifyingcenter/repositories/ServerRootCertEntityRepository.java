package com.certifyingcenter.certifyingcenter.repositories;

import com.certifyingcenter.certifyingcenter.entryies.ServerRootCertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRootCertEntityRepository extends JpaRepository<ServerRootCertEntity, String> {
    public ServerRootCertEntity findByCertName(String certName);
}
