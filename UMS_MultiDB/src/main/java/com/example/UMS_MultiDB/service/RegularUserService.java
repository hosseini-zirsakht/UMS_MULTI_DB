package com.example.UMS_MultiDB.service;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;

import java.util.List;
import java.util.Optional;

public interface RegularUserService extends BaseService {

    Person register(PersonRequestDTO personRequestDTO);

    Person approvePerson(String nationalCode);

    List<PersonResponseDTO> getPendingPerson();

    PersonResponseDTO convertToDTO(Person person);

    @Override
    Person authenticate(String username, String password);

    @Override
    Optional<Person> findByUsername(String username);

    @Override
    Optional<Person> findUserByNationalCode(String nationalCode);

    @Override
    boolean updatePasswordByNationalCode(String nationalCode, String newPassword);
}
