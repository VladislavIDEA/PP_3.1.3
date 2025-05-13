package ru.kata.spring.boot_security.demo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.service.RoleServiceImp;

@Component
public class RoleConverter implements Converter<String, Role> {

    private final RoleServiceImp roleService;

    public RoleConverter(RoleServiceImp roleService) {
        this.roleService = roleService;
    }

    @Override
    public Role convert(String source) {
        return roleService.findByName(source);
    }
}
