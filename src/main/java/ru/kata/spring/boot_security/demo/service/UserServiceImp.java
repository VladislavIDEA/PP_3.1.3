package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceImp roleService;

    @Autowired
    public UserServiceImp(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleServiceImp roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Transactional
    @Override
    public void add(User user) {
        if (user.getRoles().isEmpty()) {
            Role defaultRole = roleService.findByName("ROLE_USER");
            user.getRoles().add(defaultRole);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void update(int id, User updatedUser) {
        User existingUser = findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Обновляем основные поля
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setEmail(updatedUser.getEmail());

        // Обновляем пароль только если он не пустой
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Обновляем роли если они были переданы
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUser.getRoles());
        }

        userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void addUserWithRoles(User user, List<String> roleNames) {
        Set<Role> roles = getRolesFromNames(roleNames);
        user.setRoles(roles);
        add(user);
    }

    @Override
    @Transactional
    public void updateUserWithRoles(int id, User user, List<String> roleNames) {
        Optional<User> existingUser = findByEmail(user.getEmail());
        if (existingUser.isPresent() && existingUser.get().getId() != id) {
            throw new IllegalArgumentException("Email уже используется другим пользователем");
        }

        if (roleNames != null && !roleNames.isEmpty()) {
            Set<Role> roles = getRolesFromNames(roleNames);
            user.setRoles(roles);
        }

        update(id, user);
    }

    private Set<Role> getRolesFromNames(List<String> roleNames) {
        return roleNames.stream()
                .map(roleService::findByName)
                .collect(Collectors.toSet());
    }

}