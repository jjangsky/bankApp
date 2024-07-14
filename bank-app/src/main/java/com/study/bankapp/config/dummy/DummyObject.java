package com.study.bankapp.config.dummy;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyObject {
    protected User newUser(String username, String fullname){
        // 진짜 유저 생성
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encPassword = bCryptPasswordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected User newMockUser(Long id, String username, String fullname){
        // 가짜 유저 생성
        // Stubbing 과정에서 유저 생성 Method가 필요할 때 사용
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encPassword = bCryptPasswordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }

}
