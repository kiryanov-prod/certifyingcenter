package com.certifyingcenter.certifyingcenter.controllers;

import com.certifyingcenter.certifyingcenter.entryies.Role;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.models.AjaxChangeRoleModel;
import com.certifyingcenter.certifyingcenter.models.UserForAdminPanel;
import com.certifyingcenter.certifyingcenter.repositories.RoleRepositories;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SuperAdminController {
    private final UserRepositories userRepositories;
    private final RoleRepositories roleRepositories;

    @Autowired
    public SuperAdminController(UserRepositories userRepositories, RoleRepositories roleRepositories) {
        this.userRepositories = userRepositories;
        this.roleRepositories = roleRepositories;
    }

    @GetMapping("/admin/list_users")
    public String getAdminPageUsers(Model model){
        List<User> allUser = userRepositories.findAll();
        List<UserForAdminPanel> userForAdminPanelList =
                allUser.stream().map(u->{
                    UserForAdminPanel userForAdminPanel = new UserForAdminPanel();
                    userForAdminPanel.setId(u.getId());
                    userForAdminPanel.setUsername(u.getUsername());
                    userForAdminPanel.setFistName(u.getFirstName());
                    userForAdminPanel.setLastName(u.getLastName());
                    Role roleAdmin = roleRepositories.findRoleById(2);
                    userForAdminPanel.setAdmin(u.getRoles().contains(roleAdmin));
                    return userForAdminPanel;
                }).collect(Collectors.toList());

        model.addAttribute("users", userForAdminPanelList);

        return "admin/list_users_page";
    }


    @PostMapping("/admin/user_change")
    @ResponseBody
    public AjaxChangeRoleModel changingUserRole(@RequestBody AjaxChangeRoleModel ajaxChangeRoleModel){
        User mutableUser = userRepositories.findById(ajaxChangeRoleModel.getUserId()).orElse(null);
        Role roleAdmin = roleRepositories.findRoleById(2);
        if(mutableUser!=null){
            System.out.println(ajaxChangeRoleModel.isAdmin());

            if(ajaxChangeRoleModel.isAdmin()){
                mutableUser.getRoles().add(roleAdmin);
            }else{
                mutableUser.getRoles().remove(roleAdmin);
            }
            System.out.println(mutableUser.getRoles().size());
            userRepositories.save(mutableUser);
        }

        return ajaxChangeRoleModel;
    }

}
