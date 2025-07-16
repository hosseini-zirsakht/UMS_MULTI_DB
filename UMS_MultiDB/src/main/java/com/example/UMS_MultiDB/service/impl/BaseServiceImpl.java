package com.example.UMS_MultiDB.service.impl;

import com.example.UMS_MultiDB.exception.UserException;
import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
import com.example.UMS_MultiDB.repository.ExcelPersonRepository;
import com.example.UMS_MultiDB.repository.dev.DevPersonRepository;
import com.example.UMS_MultiDB.repository.main.MainPersonRepository;
//import com.example.UMS_MultiDB.repository.test.TestPersonRepository;
import com.example.UMS_MultiDB.service.BaseService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.management.Query.or;

@Service
@Primary
public class BaseServiceImpl implements BaseService {

    private final MainPersonRepository mainPersonRepository;
    private final DevPersonRepository devPersonRepository;
//    private final TestPersonRepository testPersonRepository;
    private final ExcelPersonRepository excelPersonRepository;
    private final PasswordEncoder passwordEncoder;


    public BaseServiceImpl(MainPersonRepository mainPersonRepository, DevPersonRepository devPersonRepository, ExcelPersonRepository excelPersonRepository
//                           TestPersonRepository testPersonRepository
    ) {
        this.mainPersonRepository = mainPersonRepository;
        this.devPersonRepository = devPersonRepository;
        this.excelPersonRepository = excelPersonRepository;
//        this.testPersonRepository = testPersonRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public Person authenticate(String username, String password) {
        Optional<Person> personOptional = excelPersonRepository.findByUsername(username)
                .or(() -> mainPersonRepository.findByUsername(username))
                .or(() -> devPersonRepository.findByUsername(username));

        if (personOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Person person = personOptional.get();

        // حذف بررسی existsById اگر نیاز نیست
        if (!passwordEncoder.matches(password, person.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (person.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Account not approved");
        }

        return person;
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        Optional<Person> person = mainPersonRepository.findByUsername(username);
        if (person.isPresent()) return person;

        person = excelPersonRepository.findByUsername(username);
        if (person.isPresent()) return person;

        return devPersonRepository.findByUsername(username);
    }

    @Override
    public Optional<Person> findUserByNationalCode(String nationalCode) {
        Optional<Person> person = mainPersonRepository.findByNationalCode(nationalCode);
        if (person.isPresent()) return person;

        person = excelPersonRepository.findUserByNationalCode(nationalCode);
        if (person.isPresent()) return person;

        return devPersonRepository.findByNationalCode(nationalCode);
    }

    @Override
    public boolean updatePasswordByNationalCode(String nationalCode, String newPassword) {
        Optional<Person> person = findUserByNationalCode(nationalCode);
        if (person.isPresent()) {
            Person user = person.get();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            if (user.getRole() == Role.REGULAR_USER) {
                mainPersonRepository.save(user);
            } else if (user.getRole() == Role.DEVELOPER) {
                devPersonRepository.save(user);
            } else if (user.getRole() == Role.TEST_USER) {
                excelPersonRepository.save(user);
            }
            return true;
        }
        return false;
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
    public boolean isValidUpdate(String currentNationalCode, PersonRequestDTO requestDTO) {
        if (!currentNationalCode.equals(requestDTO.getNationalCode())) {
            return findUserByNationalCode(requestDTO.getNationalCode()).isEmpty();
        }
        return true;
    }


    @Override
    @Transactional
    public boolean updateByNationalCode(String nationalCode, PersonRequestDTO personRequestDTO) {
        Optional<Person> optionalPerson = findUserByNationalCode(nationalCode);
        if (optionalPerson.isEmpty()) {
            return false;
        }

        Person existingPerson = optionalPerson.get();

        existingPerson.setFirstName(personRequestDTO.getFirstName());
        existingPerson.setLastName(personRequestDTO.getLastName());
        existingPerson.setEmail(personRequestDTO.getEmail());
        existingPerson.setPhoneNumber(personRequestDTO.getPhoneNumber());
        existingPerson.setUsername(personRequestDTO.getUsername());
        existingPerson.setNationalCode(personRequestDTO.getNationalCode());

        if (personRequestDTO.getPassword() != null && !personRequestDTO.getPassword().isEmpty()) {
            existingPerson.setPassword(passwordEncoder.encode(personRequestDTO.getPassword()));
        }

        switch (existingPerson.getRole()) {
            case REGULAR_USER -> mainPersonRepository.save(existingPerson);
            case DEVELOPER -> devPersonRepository.save(existingPerson);
            case TEST_USER -> excelPersonRepository.save(existingPerson);
            default -> throw new IllegalArgumentException("Invalid user role");
        }

        return true;
    }

    @Override
    public List<PersonResponseDTO> getApprovedPerson() {
        List<Person> approvedPersons = new ArrayList<>();

        approvedPersons.addAll(mainPersonRepository.getAllByStatus(Status.APPROVED));

        approvedPersons.addAll(devPersonRepository.getAllByStatus(Status.APPROVED));

        approvedPersons.addAll(excelPersonRepository.getAllByStatus(Status.APPROVED));

        return approvedPersons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByNationalCode(String nationalCode) {
        Optional<Person> person = findUserByNationalCode(nationalCode);

        if (person.isPresent()) {
            switch (person.get().getRole()) {
                case REGULAR_USER -> mainPersonRepository.deleteByNationalCode(nationalCode);
                case DEVELOPER -> devPersonRepository.deleteByNationalCode(nationalCode);
                case TEST_USER -> excelPersonRepository.deleteByNationalCode(nationalCode);
                default -> throw new IllegalArgumentException("Invalid user role");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Optional<Person> checkAccount(String nationalCode, String phoneNumber) {

        Optional<Person> person = this.findUserByNationalCode(nationalCode);
        Person person1 = person.get();

        if (phoneNumber.equals(person1.getPhoneNumber())){
            return Optional.of(person1);
        } else {
            throw new UserException("user not found");
        }

    }
}
