package com.study.bankapp.service;

import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.account.AccountRepository;
import com.study.bankapp.domain.transaction.Transaction;
import com.study.bankapp.domain.transaction.TransactionEnum;
import com.study.bankapp.domain.transaction.TransactionRespository;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.account.AccountRequestDto.AccountDepositReqDto;
import com.study.bankapp.dto.account.AccountRequestDto.AccountListRespDto;
import com.study.bankapp.dto.account.AccountRequestDto.AccountSaveReqDto;
import com.study.bankapp.dto.account.AccountResponseDto.AccountDepositRespDto;
import com.study.bankapp.dto.account.AccountResponseDto.AccountSaveRespDto;
import com.study.bankapp.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRespository transactionRespository;

    public AccountListRespDto getAccountListByUser(Long userId){
        User userPS = userRepository.findById(userId).orElseThrow(
                ()-> new CustomApiException("유저를 찾을 수 없습니다.")
        );

        // 유저의 모든 계좌 목록
        List<Account> accountList = accountRepository.findByUser_id(userId);
        return new AccountListRespDto(userPS, accountList);
    }


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

    @Transactional
    public void deleteAccount(Long number, Long userId){
        // 계좌 확인
        Account accountPs = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다.")
        );

        // 계좌 소유자 확인
        accountPs.checkOwner(userId);

        // 계좌 삭제
        accountRepository.deleteById(accountPs.getId());

    }

    @Transactional
    public AccountDepositRespDto insertAccountPrice(AccountDepositReqDto accountDepositReqDto){
        // 0원 체크
        if(accountDepositReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 입금 계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다.")
                );

        // 입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
        depositAccountPS.deposit(accountDepositReqDto.getAmount());

        // 거래 내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(null)
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(depositAccountPS.getNumber()+"")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRespository.save(transaction);
        return new AccountDepositRespDto(depositAccountPS, transactionPS);

    }

    @Transactional
    public AccountWithdrawRespDto accountwWthdraw(AccountWithdrawReqDto accountWithdrawReqDto, Long userid){
        // 0원 체크
        if(accountWithdrawReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 출금 계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다.")
                );
        
        // 출금 소유자 확인 - 로그인 사람과 동일
        withdrawAccountPS.checkOwner(userid);
        
        // 출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());
        
        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());
        
        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());
        
        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountWithdrawReqDto.getNumber() + "")
                .receiver(null)
                .build();

        Transaction transactionPS = transactionRespository.save(transaction);

        // DTO 응답
        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);

    }


}
