package com.cos.security1.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login요청이 오면 자동으로 UserDetailsService 타입으로 IOC되어 있는 loadUserByUsername 함수가 실행된다.
// /login을 로그인을 요청하면 loadUserByUsername으로 form의 name(username)이 넘어온다. 반드시 username으로 해야한다.

// /login 요청 (by config.java) > loadUserByUsername (by UserDetailsService)
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;

	// 시큐리티 session(내부 Authentication(내부 UserDetails))
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어 진다.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		if(userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}

}
