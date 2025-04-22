package com.example.AuthenticationService.dto;

import com.example.AuthenticationService.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSyncDto {
    private String userId;
    private String email;
    private Role userType; // or map to enum used by the other service if shared
}
