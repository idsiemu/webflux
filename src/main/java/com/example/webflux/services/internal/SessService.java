package com.example.webflux.services.internal;

import com.example.webflux.types.user.UserSessionTypes;
import com.example.webflux.utils.keys.SESSION;
import com.example.webflux.vo.SessionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class SessService extends Workspace {

    public <T extends SessionRequest> T makeData(ServerRequest request, Map post, Class<T> classObject){
        post.putAll(request.queryParams().toSingleValueMap());
        String lang = (String)post.get("lang");
        String sessType = (String)post.get("session_type");
        post.put("lang", (lang == null || "".equals(lang)) ? "kr" : lang.toLowerCase());
        try{
            List<String> accessToken = request.headers().header(SESSION.TOKEN_NAME);
            List<String> refreshToken = request.headers().header(SESSION.REFRESH_NAME);
            if(accessToken.size() > 0) post.put(SESSION.TOKEN_NAME, accessToken.get(0));
            if(refreshToken.size() > 0) post.put(SESSION.REFRESH_NAME, refreshToken.get(0));
        }catch (Exception e){}
        UserSessionTypes userSessionTypes = UserSessionTypes.getSession(sessType);
        if(userSessionTypes == null){
            userSessionTypes = UserSessionTypes.WEB;
        }
        T requestObject = null;
        try {
            requestObject = (T)SessionRequest.makeSessionRequest(request, post, userSessionTypes, classObject);
        } catch (Exception e) {
            writeError(post, "");
        }
        return requestObject;
    }

    public <T extends SessionRequest> T makeData(ServerRequest req, Class<T> classObject){
        return makeData(req, new HashMap(), classObject);
    }

    public Map returnData(SessionRequest sessionRequest){
        return sessionRequest.getResponse();
    }

    public <T> T returnGeneric(SessionRequest sessionRequest){
        T responseGeneric = (T) sessionRequest.getResponseGeneric();
        return responseGeneric == null ? (T)new HashMap() : responseGeneric;
    }
}
