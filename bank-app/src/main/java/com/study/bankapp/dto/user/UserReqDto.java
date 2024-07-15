package com.study.bankapp.dto.user;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserReqDto {


    @Getter
    @Setter
    public static class LoginReqDto{
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class JoinReqDto{
        // 영문, 숫자는 되고, 길이 최소 2~20자 이내
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해 주십시오.")
        @NotEmpty
        private String username;
        // 길이 4~20
        @NotEmpty
        @Size(min=4, max = 20)
        private String password;
        // 이메일 형식
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 입력해 주십시오.")
        private String email;
        // 영어, 한글, 1~20
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "영문/숫자 1~20자 이내로 작성해 주십시오.")
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
