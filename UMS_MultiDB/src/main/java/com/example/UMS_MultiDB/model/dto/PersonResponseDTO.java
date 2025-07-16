package com.example.UMS_MultiDB.model.dto;

import com.example.UMS_MultiDB.model.enums.Role;
import com.example.UMS_MultiDB.model.enums.Status;
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
public class PersonResponseDTO {

    private String lastName;
    private String firstName;
    private String nationalCode;
    private String phoneNumber;
    private String username;
    private String email;
    private Status status;
    private Role role;


}
