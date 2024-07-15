package com.study.bankapp.config.jwt;

import com.study.bankapp.config.auth.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * 모든 주소에서 동작함(토큰 검증 필터)
 */
public class jwtAuthorizationFilter extends BasicAuthenticationFilter {


    public jwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if(isHeaderVerify(request, response)){
            // 토큰이 존재하는 경우
            String token = request.getHeader(JwtVo.Header).replace(JwtVo.TOKEN_PREFIX, ""); // Bearer 제거
            LoginUser loginUser = JwtProcess.verify(token);

            // 임시 세션
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication); // 강제 로그인 처리
        }
        chain.doFilter(request, response);

    }

    // 토큰 검증 메소드
    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(JwtVo.Header);
        if (header == null || !header.startsWith(JwtVo.TOKEN_PREFIX)){
            return false;
        }else {
            return true;
        }
    }
}
