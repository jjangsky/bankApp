package com.study.bankapp.config.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.bankapp.config.dummy.DummyObject;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.user.UserReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Test
    public void setUp() throws Exception{
        userRepository.save(newUser("ssar", "쌀"));
    }

    @Test
    public void successfulAuthentication_test() throws Exception {


        // given
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername("ssar");
        loginReqDto.setPassword("1234");
        String requestBody = om.writeValueAsString(loginReqDto);

        // when
        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVo.Header);

        // then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVo.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));

    }

    @Test
    public void unsuccessfulAuthentication_test(){
        // given

        // when

        // then
    }

}