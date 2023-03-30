package com.cos.security1.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// ��ť��Ƽ �������� loginProcessingUrl("/login");
// /login��û�� ���� �ڵ����� UserDetailsService Ÿ������ IOC�Ǿ� �ִ� loadUserByUsername �Լ��� ����ȴ�.
// /login�� �α����� ��û�ϸ� loadUserByUsername���� form�� name(username)�� �Ѿ�´�. �ݵ�� username���� �ؾ��Ѵ�.

// /login ��û (by config.java) > loadUserByUsername (by UserDetailsService)
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;

	// ��ť��Ƽ session(���� Authentication(���� UserDetails))
	// �Լ� ����� @AuthenticationPrincipal ������̼��� ����� ����.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		if(userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		return null;
	}

}
