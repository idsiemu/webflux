package com.example.webflux.vo.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    String serviceToken;
    String refreshToken;
}
