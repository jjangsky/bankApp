package com.study.bankapp.dto.user;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserReqDto {

    @Getter
    @Setter
    public static class JoinReqDto{
        // 영문, 숫자는 되고, 길이 최소 2~20자 이내
        @Pattern(regexp = "", message = "영문/숫자 2~20자 이내로 작성해 주십시오.")
        @NotEmpty
        private String username;
        // 길이 4~20
        @NotEmpty
        private String password;
        // 이메일 형식
        @NotEmpty
        private String email;
        // 영어, 한글, 1~20
        @NotEmpty
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
