package com.study.bankapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bankapp.config.jwt.JwtAuthenticationFilter;
import com.study.bankapp.domain.user.UserEnum;
import com.study.bankapp.dto.ResponseDto;
import com.study.bankapp.util.CustomResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록 필요

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager,HttpSecurity>{
        // 해당 클래스를 상속받고 필터 등록 해야함
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            // 필터를 등록하기 위해 AuthenticationManager가 필요
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager)); // JWT 필터 등록
            super.configure(builder);
        }

        public HttpSecurity build(){
            return getBuilder();
        }
    }


    // JWT 서버를 만들 예정, Session 사용X
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));   // iframe 허용 안함
        http.csrf(cf->cf.disable());      // enable이면 post 맨 작동 안함
        http.cors(co->co.configurationSource(configurationSource()));

        // JSessionId를 서버쪽에서 관리 안하겠다는 뜻
        http.sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // react, 앱으로 요청할 예정
        http.formLogin(f->f.disable());
        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic(hb->hb.disable());

        // 필터 등록
        http.with(new CustomSecurityFilterManager(), c-> c.build());


        // 인증 실패
        http.exceptionHandling(e-> e.authenticationEntryPoint((request, response, authException) -> {
//            ObjectMapper om = new ObjectMapper();
//            ResponseDto<?> responseDto = new ResponseDto<>(-1, "권한없음", null);
//            String responseBody = om.writeValueAsString(responseDto);
//            response.setStatus(401);
//            response.setStatus(401);
//            response.getWriter().println(responseBody);

            // Method로 빼냄
            CustomResponseUtil.unAuthentication(response, "로그인을 진행해 주세요");
        }));




        http.authorizeHttpRequests(c->
                c.requestMatchers("/api/s/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                        .anyRequest().permitAll()
        );


        return http.build();

    }

    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT ,DELETE 허용
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true);  // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
