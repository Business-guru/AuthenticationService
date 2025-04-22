package com.example.AuthenticationService.dto;

import com.example.AuthenticationService.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String userIdentity;


    private String emailId;


    private String password;


    private boolean isVerified ;

    private Role role;

}
