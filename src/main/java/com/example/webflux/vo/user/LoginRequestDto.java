package com.example.webflux.vo.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    String id;
    String password;
}
