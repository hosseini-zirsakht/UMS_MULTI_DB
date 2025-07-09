package com.example.UMS_MultiDB.service;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;

public interface RegularUserService {

    Person register(PersonRequestDTO personRequestDTO);
}
