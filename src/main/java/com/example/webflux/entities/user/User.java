package com.example.webflux.entities.user;

import com.example.webflux.types.user.UserSessionTypes;
import com.example.webflux.utils.keys.SESSION;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user", catalog = SESSION.SCHEME_SERVICE)
public class User {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", length = 11, columnDefinition = "UNSIGNED INT(11)", nullable = false, unique = true)
    private Integer userIdx;

    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "deleted_at", nullable = false, columnDefinition="TIMESTAMP")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Boolean validPassword(String password){
        String _password = this.password == null ? "" : this.password;
        return BCrypt.checkpw(password, _password.replaceFirst("2y", "2a"));
    }

    public void setEncryptPassword(String password){
        this.password = BCrypt.hashpw(password, BCrypt.gensalt()).replaceFirst("2y", "2a");
    }

    public UserSession newSession(UserSessionTypes userSessionTypes) throws UnsupportedEncodingException {
        UserSession userSession = new UserSession();
        userSession.setSessionType(userSessionTypes);
        userSession.setUserIdx(this.userIdx);
        userSession.setUser(this);
        userSession.makeSessionKey();
        return userSession;
    }
}
