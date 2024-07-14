package com.study.bankapp.config.auth;


import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 시큐리티로 로그인이 될 때, 시큐리티가 loadUserByUsername() 을 실행해서 username 체크함
    // 없으면 오류지만 존재할 경우 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 로그인시, 세션 생성 로직

        User userPS = userRepository.findByUsername(username).orElseThrow(
                ()->new InternalAuthenticationServiceException(username)
        );

        // 조회 성공 시, 로그인 세션 객체가 생성
        return new LoginUser(userPS);
    }
}
