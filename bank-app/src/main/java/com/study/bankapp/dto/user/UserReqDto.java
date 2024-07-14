package com.study.bankapp.dto.user;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserReqDto {

    @Getter
    @Setter
    public static class JoinReqDto{
        private String username;
        private String password;
        private String email;
        private String fullname;


        // DTO를 Entity로 Builder를 사용해서 변환
        public User toEntity(BCryptPasswordEncoder passwordEncoder){
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }

    }
}
