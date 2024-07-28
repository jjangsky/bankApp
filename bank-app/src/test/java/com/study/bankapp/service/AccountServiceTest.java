package com.study.bankapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bankapp.config.dummy.DummyObject;
import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.account.AccountRepository;
import com.study.bankapp.domain.transaction.Transaction;
import com.study.bankapp.domain.transaction.TransactionRespository;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.account.AccountRequestDto;
import com.study.bankapp.dto.account.AccountRequestDto.AccountSaveReqDto;
import com.study.bankapp.dto.account.AccountResponseDto;
import com.study.bankapp.handler.ex.CustomApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks // 모든 Mock들이 InjectMock으로 주입됨
    private AccountService accountService;

    @Mock // 가짜 객체 주입
    private UserRepository userRepository;

    @Mock // 가짜 객체 주입
    private AccountRepository accountRepository;

    @Mock
    private TransactionRespository transactionRespository;

    @Spy // 진짜 객체 주입
    private ObjectMapper om;

    @Test
    public void registAccountTest() throws JsonProcessingException {
        // given
        Long userId = 1L;
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        // stub 1
        User ssar = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

        // stub 2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        // stub 3
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.save(any())).thenReturn(ssarAccount);

        // when
        AccountResponseDto.AccountSaveRespDto accountSaveRespDto = accountService.creatAccount(accountSaveReqDto, userId);
        String responseBody = om.writeValueAsString(accountSaveRespDto);

        // then
        assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
    }

    @DisplayName("계좌 삭제 테스트")
    @Test
    public void deleteAccountTest(){
        // given
        Long number = 1111L;
        Long userId = 1L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        // when, then
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(number, userId));

    }

    @DisplayName("계좌 입금 테스트")
    @Test
    public void insertAccountPriceTest(){
        // given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01012341234");

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

        /**
         * 스터빙이 진행될 때 마다 연관 객체는 새로 만들어서 주입해야 한다.
         * -> 타이밍 때문에 꼬일 수 있다.
         */
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactionRespository.save(any())).thenReturn(transaction);

        // when
        AccountDepositRespDto accountDepositRespDto = accountService.insertAccountPrice(accountDepositReqDto);
        System.out.println("잔액 : " + accountDepositRespDto.getTransaction().getDepositAccountBalance());


        // then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);


    }

}