package com.cos.security1.config.auth;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.oauth.FaceBookUserInfo;
import com.cos.security1.config.oauth.GoogleUserInfo;
import com.cos.security1.config.oauth.NaverUserInfo;
import com.cos.security1.config.oauth.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.oauth.PrincipalDetails;
import com.cos.security1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalAuth2UserService extends DefaultOAuth2UserService{

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// ���۷� ���� ���� userRequest �����Ϳ� ���� ��ó�� �Լ� 
	// �Լ� ����� @AuthenticationPrincipal ������̼��� ����� ����.
	@Override
	public PrincipalDetails loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		System.out.println("attributes : " + oAuth2User.getAttributes());
		
		OAuth2UserInfo oauth2UserInfo = null;
		if("google".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("���� �α��� ��û");
			oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if ("facebook".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("���̽��� �α��� ��û");
			oauth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
		} else if ("naver".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("���̹� �α��� ��û");
			oauth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
		} 
		else {
			System.out.println("�츮�� ���۰� ���̽��ϸ� �����մϴ� ����");
		}
		
		// User Entity ���� 
		String provider = oauth2UserInfo.getProvider(); // ���� ���� EX) google, naver , kakao
		String providerId = oauth2UserInfo.getProviderId();
		String username = provider + "_" + providerId; // google_providerId , �̷��� �ۼ��ϸ� �ߺ��� ����
		String email = oauth2UserInfo.getEmail();
		
		// ����ڴ� SNS�� �����ϱ� ������, �н������ �ʿ������, ��ť��Ƽ ��å�� ��ȣȭ�� �н����� �ʿ���
		// �������� �����ϴ� ���ڿ��� �־��ָ� �ȴ�.
		String password = bCryptPasswordEncoder.encode("���ε���"); 
		String role = "ROLE_USER";
		
		// ȸ�� ��ȸ
		User userEntity = userRepository.findByUsername(username);
		
		// �ڵ� ȸ������
		if(userEntity == null) {
			System.out.println("Oauth2 �α����� ���� �Դϴ�.");
			userEntity = User.builder()
				.username(username)
				.password(password)
				.email(email)
				.role(role)
				.provider(provider)
				.providerId(providerId)
				.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("Oauth2 �α����� �̹� ������ �ֽ��ϴ�. ����� �ڵ� ȸ�������� �Ǿ��ֽ��ϴ�.");
		}
		
		// OAuth2User��ü�� return����� �Ѵ�.
		// PrincipalDetails�� OAuth2User��ü�� ���� �ϵ��� �ߴ�.
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
}

