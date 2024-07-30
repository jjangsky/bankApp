package com.study.bankapp.web;

import com.study.bankapp.config.auth.LoginUser;
import com.study.bankapp.dto.ResponseDto;
import com.study.bankapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;


@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                                         @AuthenticationPrincipal LoginUser loginUser,
                                         BindingResult bindingResult){
        AccountSaveRespDto accountSaveRespDto = accountService.creatAccount(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser){
        AccountListRespDto accountListRespDto = accountService.getAccountListByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록 보기 유저별 성공", accountListRespDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser){
        accountService.deleteAccount(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public  ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto
            , BindingResult bindingResult){
        AccountDepositRespDto accountDepositRespDto = accountService.insertAccountPrice(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public  ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal LoginUser loginUser){
        // `@Valid` 뒤에 바로 인증 어노테이션 붙은 LoginUser 붙으면 안됨
        AccountWithdrawRespDto accountWithdrawRespDto = accountService.accountwWthdraw(accountWithdrawReqDto
                ,   loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountWithdrawRespDto), HttpStatus.CREATED);
    }

}
