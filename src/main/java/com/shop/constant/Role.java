package com.shop.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), SOCIAL("ROLE_SOCIAL");  // 일반 유저인지, 관리자인지, 소셜 로그인 유저인지 구분하기 위함

    private final String value;
}
