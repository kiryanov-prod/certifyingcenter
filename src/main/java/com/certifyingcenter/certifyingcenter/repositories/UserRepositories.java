package com.certifyingcenter.certifyingcenter.repositories;

import com.certifyingcenter.certifyingcenter.entryies.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositories  extends JpaRepository<User, Long> {
     User findByUsername(String username);
}
