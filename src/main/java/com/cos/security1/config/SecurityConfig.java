package com.cos.security1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.cos.security1.config.auth.PrincipalAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration //config ������ ���� �ֳ����̼�
@EnableWebSecurity // ������ ��ť��Ƽ ���Ͱ� ������ ����ü�ο� ����� �ȴ�.

//@Secured  Ȱ��ȭ , @PreAuthorize  Ȱ��ȭ , @PostAuthorize Ȱ��ȭ
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final PrincipalAuth2UserService principalAuth2UserService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http
			.authorizeRequests() // ��� HttpRequest ��û�� ����
			.antMatchers("/user/**").authenticated() //�α��� ����(����)
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") //����(�ΰ�)
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // ����(�ΰ�)
			.anyRequest().permitAll();
		
		http
			.formLogin()
			.loginPage("/loginForm") //������ ��ť��Ƽ���� �������н� �̵��ϴ� �α��� ������
			.loginProcessingUrl("/login")// login �ּҰ� ȣ���� �Ǹ� ��ť��Ƽ�� ����ä�� ��� �α����� �����Ѵ�. login����� ���� ������ �ʾƵ� �ȴ�.
			.defaultSuccessUrl("/"); // �α��� ������ �̵��� ������
			
		http
			.oauth2Login() // oauth2 �α����� ��ť��Ƽ�� ����
			.loginPage("/loginForm") 
			.userInfoEndpoint() // ���� �α����� �Ϸ�� ���� ��ó���� �ʿ���. Tip �ڵ�X, (��������ū + ���������������O) by Oauth-client lib 
			.userService(principalAuth2UserService);
	}
}
