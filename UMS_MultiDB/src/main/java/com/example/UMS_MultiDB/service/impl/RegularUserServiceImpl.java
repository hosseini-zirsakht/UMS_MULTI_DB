package com.example.UMS_MultiDB.service.impl;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.repository.main.MainPersonRepository;
import com.example.UMS_MultiDB.service.RegularUserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (personRequestDTO.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

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

    @Override
    public Person approvePerson(String nationalCode) {
        return mainPersonRepository.findByNationalCode(nationalCode)
                .map(person -> {
                    person.setStatus(Status.APPROVED);
                    return mainPersonRepository.save(person);
                }).orElse(null);
    }

    @Override
    public List<PersonResponseDTO> getPendingPerson() {
        return mainPersonRepository.getAllByStatus(Status.PENDING)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public PersonResponseDTO convertToDTO(Person person) {
        return new PersonResponseDTO(
                person.getLastName(),
                person.getFirstName(),
                person.getNationalCode(),
                person.getPhoneNumber(),
                person.getUsername(),
                person.getEmail(),
                person.getStatus(),
                person.getRole()
        );
    }

    @Override
    public boolean updateByNationalCode(String nationalCode, PersonRequestDTO personRequestDTO) {
        return false;
    }

    @Override
    public boolean isValidUpdate(String currentNationalCode, PersonRequestDTO requestDTO) {
        return false;
    }

    @Override
    public List<PersonResponseDTO> getApprovedPerson() {
        return List.of();
    }

    @Override
    public void deleteByNationalCode(String nationalCode) {

    }

    @Override
    public Optional<Person> checkAccount(String nationalCode, String phoneNumber) {
        return Optional.empty();
    }

//    @Override
//    public void deletePerson(String nationalCode) {
//        mainPersonRepository.deleteByNationalCode(nationalCode);
//    }

    @Override
    public Person authenticate(String username, String password) {
        Optional<Person> byUsername = mainPersonRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            Person person = byUsername.get();

            if (person.getStatus() != Status.APPROVED) {
                throw new IllegalArgumentException("Username not approved yet");
            }

            if (passwordEncoder.matches(password, person.getPassword())) {
                return person;
            }else {
                throw new RuntimeException("invalid password");
            }
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        return mainPersonRepository.findByUsername(username);
    }

    @Override
    public Optional<Person> findUserByNationalCode(String nationalCode) {
        return mainPersonRepository.findByNationalCode(nationalCode);
    }

    @Override
    @Transactional
    public boolean updatePasswordByNationalCode(String nationalCode, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        int rowsUpdated = mainPersonRepository.updatePasswordByNationalCode(nationalCode, encodedPassword);
        return rowsUpdated > 0;
    }

}
