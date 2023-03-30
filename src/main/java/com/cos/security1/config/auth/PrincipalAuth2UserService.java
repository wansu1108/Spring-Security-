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
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수 
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어 진다.
	@Override
	public PrincipalDetails loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		System.out.println("attributes : " + oAuth2User.getAttributes());
		
		OAuth2UserInfo oauth2UserInfo = null;
		if("google".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("구글 로그인 요청");
			oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if ("facebook".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("페이스북 로그인 요청");
			oauth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
		} else if ("naver".equals(userRequest.getClientRegistration().getRegistrationId())){
			System.out.println("네이버 로그인 요청");
			oauth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
		} 
		else {
			System.out.println("우리는 구글과 페이스북만 지원합니다 ㅎㅎ");
		}
		
		// User Entity 정보 
		String provider = oauth2UserInfo.getProvider(); // 제공 서버 EX) google, naver , kakao
		String providerId = oauth2UserInfo.getProviderId();
		String username = provider + "_" + providerId; // google_providerId , 이렇게 작성하면 중복이 없음
		String email = oauth2UserInfo.getEmail();
		
		// 사용자는 SNS로 인증하기 때문에, 패스워드는 필요없으나, 시큐리티 정책상 암호화된 패스워가 필요함
		// 서버에서 관리하는 문자열을 넣어주면 된다.
		String password = bCryptPasswordEncoder.encode("겟인데어"); 
		String role = "ROLE_USER";
		
		// 회원 조회
		User userEntity = userRepository.findByUsername(username);
		
		// 자동 회원가입
		if(userEntity == null) {
			System.out.println("Oauth2 로그인이 최초 입니다.");
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
			System.out.println("Oauth2 로그인을 이미 한적이 있습니다. 당신은 자동 회원가입이 되어있습니다.");
		}
		
		// OAuth2User객체를 return해줘야 한다.
		// PrincipalDetails가 OAuth2User객체를 구현 하도록 했다.
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
}

