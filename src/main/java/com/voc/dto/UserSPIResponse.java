package com.voc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSPIResponse {
    private Long userId;
    private String userRegistrationNumber;
    private String userName;
    private String phoneNumber;
    private String email;
    private String password;
}