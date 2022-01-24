package com.certifyingcenter.certifyingcenter.repositories;

import com.certifyingcenter.certifyingcenter.entryies.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepositories extends JpaRepository<Role, Long> {
    public Role findRoleById(long id);
}
