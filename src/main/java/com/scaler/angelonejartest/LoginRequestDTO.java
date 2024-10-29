package com.scaler.angelonejartest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {
    private String clientcode;
    private String password;
    private String totp;
    private String privateKey;
    private String state;
}
