package com.study.bankapp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc  // Mock 환경에서 MockMvc가 등록
@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.MOCK)
class SecurityConfigTest {

    // 가짜 환경에 등록된 MockMvc를 DI함
    @Autowired
    private MockMvc mvc;


    /**
     * 서버는 일관성있게 에러가 리턴되어야 한다.
     * 즉, 내가 모르는 에러가 화면으로 날라가지 않게 제어하자
     */
    @DisplayName("API를 요청하여 인증 테스트를 진행한다.")
    @Test
    public void authentication_test() throws Exception{
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + httpStatusCode);

        // then
        assertThat(httpStatusCode).isEqualTo(401);
    }



    @Test
    public void authorization_test() throws Exception{
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/admin/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + httpStatusCode);

        // then

    }



}