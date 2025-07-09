package com.example.UMS_MultiDB.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonRequestDTO {

    @NotBlank(message = "FirstName cannot be empty")
    @Size(min = 2, max = 50, message = "The name must be between 2 and 20 characters")
    private String lastName;

    @NotBlank(message = "LastName cannot be empty")
    @Size(min = 2, max = 50, message = "The name must be between 2 and 20 characters")
    private String firstName;

    @NotBlank(message = "National ID cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "The national code must be 10 digits")
    @Column(unique = true)
    private String nationalCode;

    @NotBlank(message = "Phone number cannot be empty")
//    @Pattern(regexp = "09\\\\d{9}", message = "The phone number must be 11 digits")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password cannot be empty")
//    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not valid")
    private String email;
}
