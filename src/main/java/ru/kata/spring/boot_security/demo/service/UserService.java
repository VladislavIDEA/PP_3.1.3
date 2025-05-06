package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    User findByUsername(String username);

    void saveUserWithRoles(User user, List<Long> roleIds);

    void updateUserWithRoles(User user, List<Long> roleIds);
}
