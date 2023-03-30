package com.cos.security1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.cos.security1.config.auth.PrincipalAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration //config 설정을 위한 애노테이션
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.

//@Secured  활성화 , @PreAuthorize  활성화 , @PostAuthorize 활성화
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final PrincipalAuth2UserService principalAuth2UserService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http
			.authorizeRequests() // 모든 HttpRequest 요청에 대한
			.antMatchers("/user/**").authenticated() //로그인 유저(인증)
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") //권한(인가)
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 권한(인가)
			.anyRequest().permitAll();
		
		http
			.formLogin()
			.loginPage("/loginForm") //스프링 시큐리티에서 인증실패시 이동하는 로그인 페이지
			.loginProcessingUrl("/login")// login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행한다. login기능을 따로 만들지 않아도 된다.
			.defaultSuccessUrl("/"); // 로그인 성공시 이동할 페이지
			
		http
			.oauth2Login() // oauth2 로그인을 시큐리티에 적용
			.loginPage("/loginForm") 
			.userInfoEndpoint() // 구글 로그인이 완료된 뒤의 후처리가 필요함. Tip 코드X, (엑세스토큰 + 사용자프로필정보O) by Oauth-client lib 
			.userService(principalAuth2UserService);
	}
}
