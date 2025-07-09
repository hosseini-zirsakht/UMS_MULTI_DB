package com.example.UMS_MultiDB.service;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;

public interface AdminService {

    Boolean loginAdmin(String username, String password);

     Person register(PersonRequestDTO personRequestDTO, Role role);
}
