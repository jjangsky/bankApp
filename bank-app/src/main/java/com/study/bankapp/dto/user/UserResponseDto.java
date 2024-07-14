package com.study.bankapp.dto.user;

import com.study.bankapp.domain.user.User;
import lombok.Getter;
import lombok.Setter;

public class UserResponseDto {

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
