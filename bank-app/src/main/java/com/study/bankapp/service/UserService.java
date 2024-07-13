package com.study.bankapp.service;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserEnum;
import com.study.bankapp.domain.user.UserRespository;
import com.study.bankapp.handler.ex.CustomApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRespository userRespository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional // 트랜잭션이 메서드 시작할 때, 시작되고 종료될 때 함께 종료
    public JoinResponseDto registUser(JoinReqDto joinReqDto){
        // 1. 동일 유저 네임 존재 검사
        Optional<User> userOp = userRespository.findByUsername(joinReqDto.getUsername());
        if(userOp.isPresent()){ // 값이 존재하면 중복 되었다는 것
            throw new CustomApiException("동일한 사용자 이름이 존재합니다");
        }

        // 2. 패스워드 인코딩
        User userPS = userRespository.save(joinReqDto.toEntity(bCryptPasswordEncoder));


        // 3. DTO 응답
        return new JoinResponseDto(userPS);

    }

    @Getter
    @Setter
    public static class JoinResponseDto{
        private Long id;
        private String username;
        private String fullname;

        public JoinResponseDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }


    @Getter
    @Setter
    public static class JoinReqDto{
        private String username;
        private String password;
        private String email;
        private String fullname;


        // DTO를 Entity로 Builder를 사용해서 변환
        public User toEntity(BCryptPasswordEncoder passwordEncoder){
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }

    }

}
