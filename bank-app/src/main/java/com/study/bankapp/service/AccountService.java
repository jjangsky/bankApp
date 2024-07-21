package com.study.bankapp.service;

import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.account.AccountRepository;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.account.AccountRequestDto;
import com.study.bankapp.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto creatAccount(AccountSaveReqDto accountSaveReqDto, Long userId){
        // User가 DB에 존재하는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(
                ()-> new CustomApiException("유저를 찾을 수 없습니다.")
        );

        // 해당 계좌가 DB에 있는 중복 여부 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if(accountOP.isPresent()){
            throw new CustomApiException("해당 계좌가 이미 존재합니다.");
        }

        // 계좌 등록
        Account accountPs = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        // DTO를 응답
        return new AccountSaveRespDto(accountPs);

    }
}
