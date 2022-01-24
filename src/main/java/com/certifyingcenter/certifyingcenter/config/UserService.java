package com.certifyingcenter.certifyingcenter.config;

import com.certifyingcenter.certifyingcenter.entryies.Role;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private UserRepositories userRepositories;

    @Autowired
    public void setUserRepositories(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositories.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User do not exist");
        }

        return  new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            mapRolesToAuthorities(user.getRoles())
                        );
    }

    private Collection<? extends GrantedAuthority > mapRolesToAuthorities(Set<Role> roles){
        return roles.stream().map( r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }
}
