package com.megait.mymall.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String email;

    private String password;

    private LocalDateTime joinedAt;

    private boolean emailVerified;

    private String emailCheckToken;  // 이메일 검증에 사용할 토큰

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @ManyToMany
    private List<Item> likes = new ArrayList<>();  // 찜한 상품들

    @OneToMany(mappedBy = "member")  // Order의 member 필드와 mapping
    private List<Order> orders = new ArrayList<>(); // 주문들 (주문내역)

    @Embedded
    private Address address;

    public void generateEmailCheckToken(){
        emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String token) {
        return emailCheckToken.equals(token);
    }

    public void completeSignUp(){
        emailVerified = true;
    }
}
