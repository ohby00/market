package com.osio.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckEmailDTO {

    private String email;
    private String verificationCode;
    private Long expirationMinutes;
}
