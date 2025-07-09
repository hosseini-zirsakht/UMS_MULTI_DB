package com.example.UMS_MultiDB.config;

import com.example.UMS_MultiDB.model.enums.Role;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AdminConfig {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final Role role =  Role.ADMIN;
}
