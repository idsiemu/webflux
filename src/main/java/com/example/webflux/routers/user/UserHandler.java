package com.example.webflux.routers.user;

import com.example.webflux.services.user.UserService;
import com.example.webflux.vo.ServiceRequest;
import com.example.webflux.vo.user.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserService userService;

    public Mono<ServerResponse> howAreYou(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> userService.makeData(request, ServiceRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(userService::howAreYou)
                        .map(userService::returnGeneric), LoginResponseDto.class);
    }
}
