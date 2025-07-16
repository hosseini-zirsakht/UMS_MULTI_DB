package com.example.UMS_MultiDB.service.impl;

import com.example.UMS_MultiDB.config.AdminConfig;
import com.example.UMS_MultiDB.exception.UserException;
import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.repository.ExcelPersonRepository;
import com.example.UMS_MultiDB.repository.dev.DevPersonRepository;
import com.example.UMS_MultiDB.repository.main.MainPersonRepository;
//import com.example.UMS_MultiDB.repository.test.TestPersonRepository;
import com.example.UMS_MultiDB.service.AdminService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminConfig adminConfig;
    private final DevPersonRepository devPersonRepository;
    private final MainPersonRepository mainPersonRepository;
//    private final TestPersonRepository testPersonRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelPersonRepository excelPersonRepository;

    public AdminServiceImpl(AdminConfig adminConfig, DevPersonRepository devPersonRepository, MainPersonRepository mainPersonRepository, ExcelPersonRepository excelPersonRepository) {
        this.adminConfig = adminConfig;
        this.devPersonRepository = devPersonRepository;
        this.mainPersonRepository = mainPersonRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.excelPersonRepository = excelPersonRepository;
    }


    @Override
    public Boolean loginAdmin(String username, String password) {

        if (!adminConfig.getAdminUsername().equals(username) || !adminConfig.getAdminPassword().equals(password)) {
            throw new UserException("Invalid username or password");
        }
        return true;
    }

    @Override
    @Transactional
    public Person register(PersonRequestDTO personRequestDTO, Role role) {
        String hashedPassword = passwordEncoder.encode(personRequestDTO.getPassword());

        Person person = Person.builder()
                .firstName(personRequestDTO.getFirstName())
                .lastName(personRequestDTO.getLastName())
                .nationalCode(personRequestDTO.getNationalCode())
                .email(personRequestDTO.getEmail())
                .phoneNumber(personRequestDTO.getPhoneNumber())
                .username(personRequestDTO.getUsername())
                .password(hashedPassword)
                .status(Status.APPROVED)
                .role(role)
                .build();

        return switch (role) {
            case  REGULAR_USER -> mainPersonRepository.save(person);
            case DEVELOPER -> devPersonRepository.save(person);
            case TEST_USER -> excelPersonRepository.save(person);
            default -> throw new IllegalStateException("Unexpected role: " + role);
        };

    }

}
