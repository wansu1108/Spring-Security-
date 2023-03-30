package com.cos.security1.config.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
	
	private Map<String,Object> attributes;//oAuth2User.getAttributes
	
	public NaverUserInfo(Map<String,Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getProviderId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getname() {
		return (String) attributes.get("name");
	}
}
