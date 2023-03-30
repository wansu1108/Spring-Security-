package com.cos.security1.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.model.User;
import com.cos.security1.oauth.PrincipalDetails;
import com.cos.security1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	
	@GetMapping({"","/"})
	public String index() {
		//mustach 기본폴더 src/main/resources
		//뷰 리졸버 설정 : templates (prefix), .mustache(suffix) 생략가능
		return "index"; //src/main/resuorce/index.mustache로 되어있음 ->config를 통해 설정 수정
	}
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication , @AuthenticationPrincipal PrincipalDetails principalDetails){
		PrincipalDetails details = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("user : " + details.getUser());
		System.out.println("PrincipalDetails : " + principalDetails.getUser());
		return "사용자 로그인 세션";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User){
		OAuth2User details = (OAuth2User) authentication.getPrincipal();
		System.out.println("user : " + details.getAttributes());
		System.out.println("OAuth2User : " + oAuth2User.getAttributes());
		return "Oauth2 사용자 로그인 세션";
	}
	
	@GetMapping("/user")
	public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " + principalDetails);
		return "user";
	}
	
	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public String manager() {
		return "manager";
	}
	
	//스프링 시큐리티의 login페이지를 오버라이드
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm(User user) {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		String encPassword = bCryptPasswordEncoder.encode(user.getPassword()); // 비밀번호를 암호화 하지 않으면 스프링 시큐리티에서 로그인 할 수 없음.
		
		//user.setId(0); auto increasement
		user.setRole("ROLE_USER");
		user.setPassword(encPassword);
		
		userRepository.save(user);
		return "redirect:/loginForm";
	}
	
	// 권한 설정
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info(){ 
		return "개인정보";
	}
	
	// 여러개의 권한을 설정
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data(){ 
		return "데이터정보";
	}
}
