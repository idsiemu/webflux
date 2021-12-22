package com.example.webflux.vo;

import com.example.webflux.types.user.UserSessionTypes;
import com.example.webflux.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public abstract class SessionRequest<T>{

    private UserSessionTypes userSessionTypes;

    private String accessKey;

    private String refreshKey;

    private LocalDateTime sessNow = LocalDateTime.now();

    private Map param = new HashMap();

    private T responseGeneric;

    private Map response = new HashMap();

    private ServerRequest serverRequest;

    public static <T extends SessionRequest> SessionRequest makeSessionRequest(ServerRequest request, Map post) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return makeSessionRequest(request, post, UserSessionTypes.WEB, ServiceRequest.class);
    }

    public static <T extends SessionRequest> SessionRequest makeSessionRequest(ServerRequest request, Map post, UserSessionTypes userSessionTypes, Class<T> classes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SessionRequest sessionRequest = classes.getConstructor(null).newInstance();
        sessionRequest.setAccessKey((String)post.get(SESSION.TOKEN_NAME));
        sessionRequest.setRefreshKey((String)post.get(SESSION.REFRESH_NAME));
        sessionRequest.setParam(post);
        sessionRequest.setServerRequest(request);
        sessionRequest.setUserSessionTypes(userSessionTypes);
        return sessionRequest;
    }

}
