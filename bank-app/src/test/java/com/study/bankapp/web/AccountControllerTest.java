package com.study.bankapp.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bankapp.config.dummy.DummyObject;
import com.study.bankapp.domain.account.Account;
import com.study.bankapp.domain.account.AccountRepository;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.account.AccountRequestDto;
import com.study.bankapp.handler.ex.CustomApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import com.study.bankapp.dto.account.AccountResponseDto.*;
import com.study.bankapp.dto.account.AccountRequestDto.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp(){
        // 유저를 미리 생성해야 시큐리티 세션에서 사용될 유저를 조회할 수 있음
        User user = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스"));
        Account ssarAccount1 = accountRepository.save(newAccount(1111L, user));
        Account cosAccount1 = accountRepository.save(newAccount(2222L, cos));
    }


    // jwt 토큰을 전송 -> 인증 필터 -> 시큐리티 세션 생성

    /**
     * 테스트 코드 인증 구조
     * `@WithUserDetails` 를 사용하여 value 값의 사용자를 DB에서 조회한다.
     *  이때 사용되는 Method는 UserDetailsService 메소드를 사용하는데
     *  조회를 하기전에 사전에 사용자를 DB에 등록해야한다.
     *
     *  setupBefore = TestExecutionEvent.TEST_EXECUTION 를 설정하여
     *  saveAccount_test()가 시작되기 전 그리고 `@BeforeEach`로 사용자 생성 후 그 사이에
     *  사용자를 조회하고 시큐리티 세션을 생성할 수 있다.
     */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION) // DB에서 ssar 유저를 검색 진행, 없으면 실패
    @Test
    public void saveAccount_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountSaveReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/s/account")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        resultActions.andExpect(status().isCreated());

    }

    /**
     * 테스트 시점에서는 insert 한 것들이 전부 Persistence Context에 올라감 - 영속화
     * 영속화 된 것들을 초기화 해주는 것이 개발 모드와 동일한 환경으로 테스트 할 수 있게 해준다.
     * 최초 select는 쿼리가 발생하지만 Persistenc Context에 존재하면 1차 캐시를 함
     * Lazy 로딩은 쿼리도 발생 안함 - Persistence Context에 존재하면
     * Lazy 로딩할 때 Persistence Context에 존재하지 않으면 쿼리가 발생함
     */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test() throws Exception{
        // given
        Long number = 1111L;

        // when
        ResultActions resultActions = mvc.perform(delete("/api/s/account/" + number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        // JUnit 테스트에서 delete 쿼리는 DB관련으로 가장 마지막에 실행되면 발동 안된다.
        assertThrows(CustomApiException.class, ()-> accountRepository.findByNumber(number).orElseThrow(
                ()-> new CustomApiException("계좌를 찾을 수 없습니다")
        ));

    }

}