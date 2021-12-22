package com.example.webflux.types.user;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

public class UserSessionsPK implements Serializable {

    @Column(name = "user_idx")
    private Integer userIdx;

    @Column(name = "session_tp", columnDefinition = "enum")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionType;

}

