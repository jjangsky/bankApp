package com.study.bankapp.service;

import com.study.bankapp.config.dummy.DummyObject;
import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import com.study.bankapp.dto.user.UserReqDto;
import com.study.bankapp.dto.user.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks
    private UserService userService;

    /**
     * 보통 클래스 상단에 `@SpringBootTest` 나 `DataJpaTest` 를 사용하는데
     * Mockito를 사용하여 테스트를 하는 경우 스프링을 사용하지 않는다.
     * -> 즉, Service 계층에서 테스트 하는경우 Repository에 대한 테스트를 확인할 수 없음
     * 그래서 Mock 객체로 Repository가 된다는 가정 하에 Service 검증을 처리한다.
     */
    @Mock
    private UserRepository userRespository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void 회원가입_test(){
        // given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("김유찬");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("test@nate.com");
        joinReqDto.setFullname("김유찬님님");


        // stubbing 처리 (일종의 가정법)
        // 첫 번째 Stub
        when(userRespository.findByUsername(any())).thenReturn(Optional.empty()); // 해당 메소드를 실행하면 빈값 반환

        // 두 번째 Stub
        User ssar = newMockUser(1L, "김유찬" , "김유찬님님");
        when(userRespository.save(any())).thenReturn(ssar);

        // when
        UserResponseDto.JoinResponseDto joinResponseDto = userService.registUser(joinReqDto);

        // then
        assertThat(joinResponseDto.getId()).isEqualTo(1L);
        assertThat(joinResponseDto.getUsername()).isEqualTo("김유찬");
    }
}