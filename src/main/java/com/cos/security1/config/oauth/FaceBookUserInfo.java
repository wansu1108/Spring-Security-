package com.cos.security1.config.oauth;

import java.util.Map;

public class FaceBookUserInfo implements OAuth2UserInfo {
	
	private Map<String,Object> attributes;//oAuth2User.getAttributes
	
	public FaceBookUserInfo(Map<String,Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getProvider() {
		return "facebook";
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
