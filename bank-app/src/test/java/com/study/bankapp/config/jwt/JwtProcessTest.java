package com.study.bankapp.config.jwt;

import com.study.bankapp.config.auth.LoginUser;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtProcessTest {

    /**
     * 토큰 생성 및 검증 테스트
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void create_test() throws Exception{
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);

        // when
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println(jwtToken);

        // then
        assertTrue(jwtToken.startsWith(JwtVo.TOKEN_PREFIX)); // Bearer가 존재하는지

    }

    @Test
    public void verify_test() throws Exception{
        // given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        jwtToken = jwtToken.replace(JwtVo.TOKEN_PREFIX, "");

        // when
        LoginUser jwtUser = JwtProcess.verify(jwtToken);

        // then
        assertThat(jwtUser.getUser().getId()).isEqualTo(1L);

    }

}