package com.example.UMS_MultiDB.service;

import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;

import java.util.Optional;

public interface TestUserService extends BaseService {

    @Override
    Person authenticate(String username, String password);

    @Override
    Optional<Person> findByUsername(String username);

    @Override
    Optional<Person> findUserByNationalCode(String nationalCode);

    @Override
    boolean updatePasswordByNationalCode(String nationalCode, String newPassword);

    @Override
    PersonResponseDTO convertToDTO(Person person);
}
