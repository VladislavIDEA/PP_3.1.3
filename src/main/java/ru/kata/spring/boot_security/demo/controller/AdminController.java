package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    private AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(ModelMap model, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/access-denied";
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("user", new User());
        return "userListAdmin";
    }

    @GetMapping("/add")
    public String addUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "userListAdmin";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          BindingResult result,
                          @RequestParam("roles") List<String> roleNames,
                          ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            model.addAttribute("users", userService.findAll());
            return "userListAdmin";
        }
        try {
            userService.addUserWithRoles(user, roleNames);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleService.findAll());
            model.addAttribute("users", userService.findAll());
            return "userListAdmin";
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam("id") int id, ModelMap model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return "userEditAdmin";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam("id") int id,
                           @ModelAttribute("user") @Valid User user,
                           @RequestParam(value = "roles", required = false) List<String> roleNames,
                           BindingResult result,
                           ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "userEditAdmin";
        }

        try {
            userService.updateUserWithRoles(id, user, roleNames);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleService.findAll());
            return "userEditAdmin";
        }

        return "redirect:/admin";
    }

    @GetMapping("/delete")
    public String deleteUserForm(@RequestParam("id") int id, ModelMap model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "userDeleteAdmin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}