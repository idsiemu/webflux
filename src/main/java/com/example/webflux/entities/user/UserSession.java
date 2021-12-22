package com.example.webflux.entities.user;

import com.example.webflux.types.user.UserSessionTypeConverter;
import com.example.webflux.types.user.UserSessionTypes;
import com.example.webflux.types.user.UserSessionsPK;
import com.example.webflux.utils.crypt.Crypt;
import com.example.webflux.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_session", catalog = SESSION.SCHEME_SERVICE)
@IdClass(UserSessionsPK.class)
@Getter
@Setter
public class UserSession {

    @Id
    @Column(name = "user_idx", length = 11, columnDefinition = "UNSIGNED INT(11)", nullable = false)
    private Integer userIdx;

    @ManyToOne
    @JoinColumn(name = "user_idx", insertable = false, updatable = false)
    private User user;

    @Id
    @Column(name = "session_tp", columnDefinition = "enum")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionType;

    @Column(name = "session_key", length = 500)
    private String sessionKey;

    @Column(name = "salt", length = 256)
    private String salt;

    @Column(name = "created_at", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    public void makeSessionKey() throws UnsupportedEncodingException {
        this.salt = Crypt.newCrypt().getSalt(8);
        this.sessionKey = Jwts.builder()
                .setIssuer(SESSION.TOKEN_ISSURE)
                .setSubject(SESSION.TOKEN_NAME)
                .claim("sess_now", LocalDateTime.now())
                .setIssuedAt(new Date())
                .signWith(
                        SignatureAlgorithm.HS256,
                        this.salt.getBytes("UTF-8")
                ).compact();

    }

    public String makeAccessKey() throws UnsupportedEncodingException {
        Long expiredTime = 1000 * 60L * 60L * 2L; // 토큰 유효 시간 (2시간)
//        Long expiredTime = 1000 * 10L; // 토큰 유효 시간 (10초)

        Date ext = new Date(); // 토큰 만료 시간
        ext.setTime(ext.getTime() + expiredTime);

        String accessKey = Jwts.builder()
                .setIssuer(SESSION.TOKEN_ISSURE)
                .setSubject(SESSION.TOKEN_NAME)
                .claim("user_idx", this.userIdx)
                .claim("sess_now", LocalDateTime.now())
                .setExpiration(ext)
                .setIssuedAt(new Date())
                .signWith(
                        SignatureAlgorithm.HS256,
                        this.salt.getBytes("UTF-8")
                ).compact();

        return accessKey;
    }
}