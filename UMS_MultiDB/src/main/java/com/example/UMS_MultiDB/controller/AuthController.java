package com.example.UMS_MultiDB.controller;

import com.example.UMS_MultiDB.model.dto.LoginRequestDTO;
import com.example.UMS_MultiDB.model.entity.Person;
import com.example.UMS_MultiDB.service.BaseService;
import com.example.UMS_MultiDB.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final BaseService baseService;
    private final JwtUtils jwtUtils;

    public AuthController(BaseService baseService, JwtUtils jwtUtils) {
        this.baseService = baseService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Person person = baseService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            String token = jwtUtils.generateToken(person.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("person", baseService.convertToDTO(person));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid");
            }

            String jwtToken = authHeader.substring(7);
            String username = jwtUtils.getUsernameFromToken(jwtToken);

            Optional<Person> person = baseService.findByUsername(username);
            if (person.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(baseService.convertToDTO(person.get()));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout successful. Please remove the token from client side.");
    }

    @PostMapping("/verify-reset")
    public ResponseEntity<?> verifyReset(@RequestParam String nationalCode, @RequestParam String phoneNumber) {
        Optional<Person> person = baseService.checkAccount(nationalCode, phoneNumber);
        if (person.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person.get());
    }
}