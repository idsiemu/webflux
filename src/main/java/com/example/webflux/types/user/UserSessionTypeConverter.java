package com.example.webflux.types.user;


import javax.persistence.AttributeConverter;

public class UserSessionTypeConverter implements AttributeConverter<UserSessionTypes, String> {
    @Override
    public String convertToDatabaseColumn(UserSessionTypes userSessionTypes) {
        return userSessionTypes.getSession();
    }

    @Override
    public UserSessionTypes convertToEntityAttribute(String session) {
        return UserSessionTypes.getSession(session);
    }
}
