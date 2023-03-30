# SpringBoot-Security
1. [SpringBoot - Basic - V1](#1.-V1-스프링-시큐리티-기본)
2. [SpringBoot - Oauth - V2](#2.-V2-스프링-시큐리티-Oauth2)

# 1. V1 스프링 시큐리티 기본
+ gradle
```java
implementation 'org.springframework.boot:spring-boot-starter-security'
```

+ application.yml
```
server:
  port: 8080
  servlet:
    context-path: /
    encoding:
      charset: UTF-8
      enabled: true
      force: true
      
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3308/security?serverTimezone=Asia/Seoul
    username: id
    password: password
    
  jpa:
    hibernate:
      ddl-auto: update #create update none
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    show-sql: true
```

+ SecurityConfig

    + Security 권한 설정 방법
    ```java
    http
    .antMatchers("/user/**").authenticated() // "/users/**" 접근시 인징(authenticated) 필요
    .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // "/manager/**" 인가(권한) 필요
    .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
    ```

    + 컨트롤러에 직접 권한 설정
    ```java
    // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화 SecurityConfig.java에 설정
    @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)

    // 컨트롤러에 어노테이션 거는 법
    @PostAuthorize("hasRole('ROLE_MANAGER')")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Secured("ROLE_MANAGER")
    ```

+ User & UserDetails & UserDetialsService
    + 시큐리티 설정에서 loginProcessingUrl("/login")의해 요청이 오면 UserDetailsService가 실행이 된다.
    + UsedDetailsSevice는 내부에 loadUserByUsername 함수를 실행하고, UserDetails를 반환 한다.
    + UserDetails는 내부에 User정보를 가지고 있다.
    + 반환한 유저 정보는 SecurityContextHolder에 내부에 저장된다
    


# 2. V2 스프링 시큐리티 Oauth2

+ VIEW
    + Oauth2 요청 주소는 정해져있다. /oauth2/authorization/provider_name
    + <a href="/oauth2/authorization/google">Google 로그인</a>
	+ <a href="/oauth2/authorization/naver">Naver 로그인</a>

+ DefaultOauth2UserService
로그인이 완료되면 유저 정보를 전달받는 콜백함수 이다.

    ```java
        @Service
        public class PrincipalAuth2UserService extends DefaultOAuth2UserService{
            // 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수 
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2User oAuth2User = super.loadUser(userRequest);
                System.out.println(userRequest.getClientRegistration()); // provider , ex)kakao , google, naver
                System.out.println(userRequest.getAccessToken()); // 엑세스 토큰 정보
                System.out.println(oAuth2User.getAttributes()); // 사용자 프로필 정보
                return super.loadUser(userRequest);
            }
        }
    ```

+ OAuth2User vs UserDetails
    + `일반 로그인:`시큐리티 세션(Authentication(UserDetails))
    + `SNS 로그인:`시큐리티 세션(Authentication(OAuth2User))
    + 해결방안은 `PrincipalDetails`에 `UserDetails` , `OAuth2User` 두가지 타입을 전부 구현하면 된다.
    ```java
        public class PrincipalDetails implements UserDetails , OAuth2User{}
    ```

