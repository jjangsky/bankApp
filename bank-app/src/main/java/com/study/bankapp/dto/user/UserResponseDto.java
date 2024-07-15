package com.study.bankapp.dto.user;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

public class UserResponseDto {

    @Getter
    @Setter
    public static class LoginRespDto{
        // 로그인 성공 후 반환 객체
        private Long id;
        private String username;
        private String createdAt;

        public LoginRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat( user.getCreatedAt());
        }
    }

    @Getter
    @Setter
    public static class JoinResponseDto{


        private Long id;
        private String username;
        private String fullname;

        public JoinResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
