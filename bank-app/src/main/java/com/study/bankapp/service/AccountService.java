package com.study.bankapp.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.account.AccountRepository;
import com.study.bankapp.domain.transaction.Transaction;
import com.study.bankapp.domain.transaction.TransactionEnum;
import com.study.bankapp.domain.transaction.TransactionRespository;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.account.AccountRequestDto;
import com.study.bankapp.handler.ex.CustomApiException;
import com.study.bankapp.util.CustomDateUtil;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Setter
    @Getter
    public static class AccountDepositRespDto{
        private Long id;
        private Long number;
        private TransactionDto transactionDto;

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transactionDto = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String reciver;
            private Long amount;
            @JsonIgnore
            private Long depositAccountBalance;  // 클라이언트 전달 X
            private String tel;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.reciver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }


    }

    @Getter
    @Setter
    public static class AccountDepositReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "^(DEPOSIT)$")
        private String gubun;
        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }
}
