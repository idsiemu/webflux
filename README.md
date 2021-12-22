# 스프링부트 webflux api 서버 세팅

### 디렉터리 구조
```
└── src/main
    ├── java
    │     └── com/example.webflux
    │           ├── config
    │           │      ├── BeansMappers - API 서버 스레드 설정입니다.
    │           │      ├── CorsGlobalConfiguration - cors 설정입니다.
    │           │      ├── ErrorGlobalHandler - API 서버 요청시 에러에 관한 일괄적 처리입니다.
    │           │      ├── RdbConfig - 연결된 데이터베이스와의 스레드풀 및 요청시간 등등에 대한 처리입니다.
    │           │      └── SqlMaster - mybatis xml에 정의된 쿼리를 요청하는 설정입니다.
    │           ├── entities - 데이터베이스의 테이블을 class로 정의해 놓은 곳
    │           ├── repositories - entity와 JPA를 열결하여 정의해 놓은 곳
    │           ├── routers - API endpoint 목록 및 해당 endpoint에 연결된 handler에 관한 설정을 정의한곳
    │           ├── services - 요청에 따른 실질적 로직을 처리하는 곳입니다.
    │           ├── type - 데이터베이스의 enum타입에 관한 converting class정의
    │           ├── utils - 각종 유틸에 관한 정의
    │           └── vo - 객체의 타입을 정의해 놓은 곳입니다.
    └── resources
            ├── mapper
            │     ├── example - mybatis에 각종 쿼리를 저장해 놓은 곳
            │     └── mybatis-context - mybatis 관련 설정을 정의해 놓은 곳
            └── application.properties - spring boot 관련 설정을 정의 해놓은 곳
```
### JPA
1. entities 디렉터리내부에 데이터베이스의 테이블을 정의합니다.
```java
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
}
```
2. 정의한 entity를 repositor에 바인딩한다.
```java
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String id);
}
```
3. services에서 필요한 repository를 가져와서 사용한다
```java
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
```
### mybatis
1. resources/mapper/example 디럭터리아래에 쿼리를 작성합니다.
```xml
<mapper namespace="example.webflux">
    <select id="getItem" parameterType="hmap" resultType="hmap">
        SELECT *
          FROM user
         WHERE idx = #{idx}
    </select>
</mapper>
``` 
2. services안에서 작성한 쿼리를 다음과 같은 방식으로 사용합니다.
```java
public ServiceRequest imFineThankYou(ServiceRequest request) {
    Map param = request.getParam();
    Map data = (Map) getItem("example.webflux.getItem", param);
    request.setResponse(data);
    return request;
}
```
### globalErrorHandle
1. config/ErrorGlobalHandler에 에러 관련 리턴에 대한 정의를 설정합니다.
2. services/internal/Workspace에 writeError 함수를 요청하면 해당 코드에 맞는 에러를 러턴하도록 처리했습니다.
```java
protected void writeError(Map map, String code, HttpStatus httpStatus){
    code = code == null || code.equals("") ? "900-001" : code;
    map.put("code", code);
    String err = (String)getItem("example.webflux.getMessage", map);

    BusinessException businessException = new BusinessException();
    businessException.setErrCode(code);
    businessException.setMsg(err == null ? "" : err);
    businessException.setErrHttpStatus(httpStatus);
    throw businessException;
}
```

