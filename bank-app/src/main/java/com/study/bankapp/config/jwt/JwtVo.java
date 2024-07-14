package com.study.bankapp.config.jwt;

import com.auth0.jwt.interfaces.Header;

public interface JwtVo {
    public static final String SECRET = "JJANGSKYSECRET";
    public static final int EXPIRATION_TIME = 1000 * 60 * 24 * 7;// 만료시간 일주일 설정
    public static final String TOKEN_PREFIX = "Bearer "; // 프로토콜 규칙상 항상 앞에 부여
    public static final String Header = "Authorization";
}
