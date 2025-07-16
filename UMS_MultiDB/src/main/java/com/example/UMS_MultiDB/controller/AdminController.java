package com.example.UMS_MultiDB.controller;

import com.example.UMS_MultiDB.model.dto.PersonRequestDTO;
import com.example.UMS_MultiDB.model.dto.PersonResponseDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.repository.main.MainPersonRepository;
import com.example.UMS_MultiDB.service.AdminService;
import com.example.UMS_MultiDB.service.BaseService;
import com.example.UMS_MultiDB.service.RegularUserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class AdminController {

    private final AdminService adminService;
    private final RegularUserService regularUserService;
    private final MainPersonRepository mainPersonRepository;
    private final BaseService baseService;

    public AdminController(AdminService adminService, RegularUserService regularUserService, MainPersonRepository mainPersonRepository, BaseService baseService) {
        this.adminService = adminService;
        this.regularUserService = regularUserService;
        this.mainPersonRepository = mainPersonRepository;
        this.baseService = baseService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(
            @RequestParam String username,
            @RequestParam String password) {

        if (adminService.loginAdmin(username, password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/person/register")
    public ResponseEntity<Person> registerPerson(
            @RequestBody PersonRequestDTO personRequestDTO) {

        return ResponseEntity.ok(adminService.register(personRequestDTO, personRequestDTO.getRole()));
    }

    @GetMapping("/pending-person")
    public ResponseEntity<List<PersonResponseDTO>> getPendingUsers() {
        List<PersonResponseDTO> pendingUsers = regularUserService.getPendingPerson();
        return ResponseEntity.ok(pendingUsers);
    }

    @PostMapping("/approve-person/{nationalCode}")
    public ResponseEntity<Person> approveUser(@PathVariable String nationalCode) {
        Person person = regularUserService.approvePerson(nationalCode);
        return person != null ? ResponseEntity.ok(person) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete-person/{nationalCode}")
    @Transactional
    public ResponseEntity<String> deletePerson(@PathVariable String nationalCode) {
        baseService.deleteByNationalCode(nationalCode);
        return ResponseEntity.ok("User deleted.");
    }


    @PutMapping("/update/person/{nationalCode}")
    public ResponseEntity<String> updatePersonByNationalCode(
            @PathVariable String nationalCode,
            @RequestBody @Valid PersonRequestDTO personRequestDTO) {

        try {
            if (!baseService.isValidUpdate(nationalCode, personRequestDTO)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The national code or information is invalid");
            }

            boolean isUpdated = baseService.updateByNationalCode(nationalCode, personRequestDTO);
            return isUpdated ?
                    ResponseEntity.ok("Update completed successfully.") :
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/persons/{nationalCode}")
    public ResponseEntity<PersonResponseDTO> getPersonByNationalCode(@PathVariable String nationalCode) {
        Optional<Person> person = baseService.findUserByNationalCode(nationalCode);
        return person.map(p -> ResponseEntity.ok(baseService.convertToDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/approved-person")
    public ResponseEntity<List<PersonResponseDTO>> getApprovedUsers() {
        List<PersonResponseDTO> approvedUsers = baseService.getApprovedPerson();
        return ResponseEntity.ok(approvedUsers);
    }

}
