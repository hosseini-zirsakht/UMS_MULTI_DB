package com.example.UMS_MultiDB.service.impl;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.repository.main.MainPersonRepository;
import com.example.UMS_MultiDB.service.RegularUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegularUserServiceImpl implements RegularUserService {

    private final MainPersonRepository mainPersonRepository;
    private final PasswordEncoder passwordEncoder;

    public RegularUserServiceImpl(MainPersonRepository mainPersonRepository) {
        this.mainPersonRepository = mainPersonRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Person register(PersonRequestDTO personRequestDTO) {

        String hashedPassword = passwordEncoder.encode(personRequestDTO.getPassword());

        Person person = Person.builder()
                .firstName(personRequestDTO.getFirstName())
                .lastName(personRequestDTO.getLastName())
                .nationalCode(personRequestDTO.getNationalCode())
                .email(personRequestDTO.getEmail())
                .phoneNumber(personRequestDTO.getPhoneNumber())
                .username(personRequestDTO.getUsername())
                .password(hashedPassword)
                .status(Status.PENDING)
                .role(Role.REGULAR_USER)
                .build();
        return mainPersonRepository.save(person);


    }

}
