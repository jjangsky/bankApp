package com.study.bankapp.dto.account;

import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.user.User;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class AccountRequestDto {

    @Getter
    @Setter
    public static class AccountSaveReqDto{

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user){
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }
}
