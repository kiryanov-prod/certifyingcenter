package com.certifyingcenter.certifyingcenter.controllers;



import com.certifyingcenter.certifyingcenter.entryies.Role;
import com.certifyingcenter.certifyingcenter.entryies.User;
import com.certifyingcenter.certifyingcenter.models.RegistryUser;
import com.certifyingcenter.certifyingcenter.repositories.RoleRepositories;
import com.certifyingcenter.certifyingcenter.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistryController {

    private UserRepositories userRepositories;
    private RoleRepositories roleRepositories;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistryController(UserRepositories userRepositories, RoleRepositories roleRepositories, PasswordEncoder passwordEncoder) {
        this.userRepositories = userRepositories;
        this.roleRepositories = roleRepositories;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registry")
    public String getRegistryPage(Model model) {
        model.addAttribute("registryObject", new RegistryUser());
        return "registry_page";
    }

    @PostMapping("/registry_end")
    public String registryUser(RegistryUser registryUser){
        User userCheck = userRepositories.findByUsername(registryUser.getUsername());
        if(userCheck!=null){
            return "redirect:/registry?nameUs";
        }
        if(!registryUser.getPassword().equals(registryUser.getConfirmPassword())){
            return "redirect:/registry?passwordError";
        }

        User user = new User();
        user.setFirstName(registryUser.getFirsName());
        user.setLastName(registryUser.getLastName());
        user.setUsername(registryUser.getUsername());
        user.setPassword(passwordEncoder.encode(registryUser.getPassword()));

        Role role = roleRepositories.findRoleById(1);

        user.getRoles().add(role);

        userRepositories.save(user);

        return "redirect:/login?successfulRegistration";
    }
}
