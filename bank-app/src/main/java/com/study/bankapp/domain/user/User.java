package com.study.bankapp.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor // 스프링이 USER 객체 생성할 때 빈 생성자로 new를 하기 때문에 필요함
@EntityListeners(AuditingEntityListener.class)  // 자동으로 시간 기록할려면 이거 필요함
@Getter
@Table(name = "user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 20)
    private String email;

    @Column(nullable = false, length = 20)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEnum role;

    @CreatedDate                // insert시 자동으로 날짜 기록
    @Column(nullable = false)
    private LocalDateTime updateAt;

    @LastModifiedDate           // update 시점에서 자동으로 날짜 기록
    @Column(nullable = false)
    private LocalDateTime createdAt;
    // TODO : 엔티티 생성 및 수정 시간은 BaseEntity로 뺄 수 있도록


    @Builder
    public User(Long id, String username, String password, String email, String fullname, UserEnum role, LocalDateTime updateAt, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
        this.updateAt = updateAt;
        this.createdAt = createdAt;
    }
}
