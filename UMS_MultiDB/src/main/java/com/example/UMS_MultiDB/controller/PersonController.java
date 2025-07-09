package com.example.UMS_MultiDB.controller;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.service.RegularUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final RegularUserService regularUserService;

    public PersonController(RegularUserService regularUserService) {
        this.regularUserService = regularUserService;
    }

    @PostMapping("/regular_user")
    public ResponseEntity<Person> registerPerson(PersonRequestDTO personRequestDTO) {
        return ResponseEntity.ok(regularUserService.register(personRequestDTO));
    }
}
