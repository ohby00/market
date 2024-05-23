package com.osio.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveDTO {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private String verificationCode;
}
