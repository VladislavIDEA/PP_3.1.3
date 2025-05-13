package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.kata.spring.boot_security.demo.converter.RoleConverter;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleConverter roleConverter;

    public WebMvcConfig(RoleConverter roleConverter) {
        this.roleConverter = roleConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(roleConverter);
    }
}
