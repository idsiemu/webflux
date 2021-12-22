package com.example.webflux.routers.user;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Component
public class UserRouter {
    @Bean
    public RouterFunction<ServerResponse> UserRouter(UserHandler handler){
        return RouterFunctions
                .route(POST("/api/how-are-you"), handler::howAreYou)
                ;
    }
}
