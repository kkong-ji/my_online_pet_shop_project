package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {     // Member 엔티티를 데이터베이스에 저장

    Member findByEmail(String email);            // 회원 가입 시 중복된 회원이 있는지 검사하기 위해
                                                // 이메일로 회원 검사 쿼리 메소드 작성

}
