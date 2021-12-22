package com.example.webflux.services.user;

import com.example.webflux.entities.user.UserSession;
import com.example.webflux.repositories.user.UserRepository;
import com.example.webflux.vo.user.LoginRequestDto;
import com.example.webflux.services.internal.SessService;
import com.example.webflux.vo.ServiceRequest;
import com.example.webflux.vo.user.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService extends SessService {

    private final UserRepository userRepository;

    public ServiceRequest howAreYou(ServiceRequest request) {
        Map param = request.getParam();
        LoginRequestDto loginRequestDto = map(param, LoginRequestDto.class);
        LoginResponseDto responseDto = new LoginResponseDto();
        userRepository.findById(loginRequestDto.getId())
                .ifPresentOrElse(user -> {
                    if(user.validPassword(loginRequestDto.getPassword())){
                        try {
                            UserSession userSession = user.newSession(request.getUserSessionTypes());
                            responseDto.setRefreshToken(userSession.getSessionKey());
                            responseDto.setServiceToken(userSession.makeAccessKey());
                        } catch (UnsupportedEncodingException e) {
                            writeError(param, "000-001");
                        }
                    }else{
                        writeError(param, "000-001");
                    }
                }, () -> {
                    writeError(param, "000-001");
                });
        request.setResponseGeneric(responseDto);
        return request;
    }
}
