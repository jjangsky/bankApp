package com.study.bankapp.config.jwt;

import com.study.bankapp.config.auth.LoginUser;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class jwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void authorization_success_test() throws Exception{
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String token = JwtProcess.create(loginUser);

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test").header(JwtVo.Header, token));
        // -> 헤더의 인증 부분에 토큰이 존재해야 요청 가능함, 아니면 401 반환됨

        // then
        resultActions.andExpect(status().isNotFound());

    }

    @Test
    public void authorization_fail_test() throws Exception{
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }
    // github 계정 인증 Test
    @Test
    public void authorization_admin_test() throws Exception{
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.ADMIN)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String token = JwtProcess.create(loginUser);

        // when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test").header(JwtVo.Header, token));

        // then
        resultActions.andExpect(status().isNotFound());
    }

}