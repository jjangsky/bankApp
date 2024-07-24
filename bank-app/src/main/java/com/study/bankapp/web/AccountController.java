package com.study.bankapp.web;

import com.study.bankapp.config.auth.LoginUser;
import com.study.bankapp.dto.ResponseDto;
import com.study.bankapp.handler.ex.CustomForbiddenException;
import com.study.bankapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        AccountService.AccountListRespDto accountListRespDto = accountService.getAccountListByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 목록 보기 유저별 성공", accountListRespDto), HttpStatus.OK);
    }
}
