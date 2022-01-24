package com.certifyingcenter.certifyingcenter.controllers;


import com.certifyingcenter.certifyingcenter.entryies.Role;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Set;

@Controller
public class RedirectController {

    private UserRepositories userRepositories;

    @Autowired
    public void setUserRepositories(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }

    @GetMapping("/default")
    public String redirectSuccessLogin(Principal principal){
        String name = principal.getName();
        User user = userRepositories.findByUsername(name);
        Set<Role> roles = user.getRoles();


        boolean isUser = false;
        boolean isAdmin = false;
        boolean isSuperAdmin = false;

        for (Role role : roles) {
            if(role.getName().equals("ROLE_USER")){
                isUser = true;
            }
            if(role.getName().equals("ROLE_ADMIN")){
               isAdmin = true;
            }
            if(role.getName().equals("ROLE_SUPER_ADMIN")){
                isSuperAdmin= true;
            }
        }
        if(isSuperAdmin){
            return "redirect:/admin/list_users";
        }
        if(isAdmin){
            return "redirect:/admin/list_certificates";
        }
        if (isUser) {
            return  "redirect:/home";
        }

        return "redirect:/main";
    }

}
