package com.study.bankapp.config.dummy;

import com.study.bankapp.domain.user.User;
import com.study.bankapp.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DummyDevInit extends DummyObject{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository){
        return (args) -> {
            // 서버 실행시에 무조건 실행된다.(profile 설정 된것만)
            User ssar = userRepository.save(newUser("ssar", "쌀"));

        };
    }
}
