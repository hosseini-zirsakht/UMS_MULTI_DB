package com.example.UMS_MultiDB.service;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;

import java.util.List;
import java.util.Optional;

public interface BaseService {


    Person authenticate(String username, String password);

    Optional<Person> findByUsername(String username);

    Optional<Person> findUserByNationalCode(String nationalCode);

    boolean updatePasswordByNationalCode(String nationalCode, String newPassword);

    PersonResponseDTO convertToDTO(Person person);

    boolean updateByNationalCode(String nationalCode, PersonRequestDTO personRequestDTO);

    boolean isValidUpdate(String currentNationalCode, PersonRequestDTO requestDTO);

    List<PersonResponseDTO> getApprovedPerson();

    void deleteByNationalCode(String nationalCode);

    Optional<Person> checkAccount(String nationalCode, String phoneNumber);
}
