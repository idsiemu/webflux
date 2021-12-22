package com.example.webflux.vo;

import com.example.webflux.entities.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest extends SessionRequest{

    private User sessUser;
}
