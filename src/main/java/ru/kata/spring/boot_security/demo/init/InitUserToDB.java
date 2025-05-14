package ru.kata.spring.boot_security.demo.init;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitUserToDB {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public InitUserToDB(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (roleService.findAll().isEmpty()) {


            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");

            // set roles
            Set<Role> rolesAdmin = new HashSet<>();
            Set<Role> rolesUser = new HashSet<>();
            rolesAdmin.add(adminRole);
            rolesUser.add(userRole);
            User admin = new User("Admin", "Admin", "admin@admin.ru", "admin", rolesAdmin);
            User user = new User("User", "User", "user@user.ru", "user", rolesUser);
            roleService.add(adminRole);
            roleService.add(userRole);
            userService.add(admin);
            userService.add(user);

        }
    }
}
