package com.study.bankapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bankapp.config.dummy.DummyObject;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.user.UserReqDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
        em.clear();
    }


    @Test
    public void join_test()throws Exception{
        // given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("love");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("love@nate.com");
        joinReqDto.setFullname("러브");

        String requestBody = om.writeValueAsString(joinReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + requestBody);

        // then
        resultActions.andExpect(status().isCreated()); // 검증


    }

    // 실패하는 경우 test
    @Test
    public void join_fail_test()throws Exception{
        // given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("love");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("lov1421e@nate.com");
        joinReqDto.setFullname("러브");

        String requestBody = om.writeValueAsString(joinReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/join")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + requestBody);

        // then
        resultActions.andExpect(status().isBadRequest());

    }

}