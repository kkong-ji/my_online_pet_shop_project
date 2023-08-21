package com.shop.service;

import com.shop.dto.MemberUpdateDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional                                              // 로직을 처리하다가 에러가 발생하였다면, 변경된 데이터를 로직을 수행하기 이전 상태로 콜백
@RequiredArgsConstructor                                    // 빈을 주입하는 방법으로는 @Autowired 어노테이션을 이용하거나, 필드주입, 생성자주입을 이용하는 방법이 있음.
public class MemberService implements UserDetailsService {  // @RequiredArgsConstructor 어노테이션은 final이나 @NonNull이 붙은 필드에 생성자를 생성할 수 있게 해줌.

    private final MemberRepository memberRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");   // 이미 가입된 회원인 경우 예외 처리
        }
    }

    /** 비밀번호 일치 확인 **/
    @ResponseBody
    public boolean checkPassword(Member member, String checkPassword) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember == null) {
            throw new IllegalStateException("없는 회원입니다.");
        }
        String realPassword = member.getPassword();
        boolean matches = passwordEncoder.matches(checkPassword, realPassword);
        System.out.println(matches);
        return matches;
    }


    /** 회원정보 수정 **/
    public Long updateMember(MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findByEmail(memberUpdateDto.getEmail());
        member.updateUsername(memberUpdateDto.getName());
        member.updateAddress(memberUpdateDto.getZipcode());
        member.updateStreetAddress(memberUpdateDto.getStreetadr());
        member.updateDetailAddress(memberUpdateDto.getDetailadr());
        member.updateOriginalPassword(memberUpdateDto.getPassword());

        // 회원 비밀번호 수정을 위한 패스워드 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePw = encoder.encode(memberUpdateDto.getPassword());
        member.updatePassword(encodePw);

        memberRepository.save(member);

        return member.getId();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {      // UserDetailsService 인터페이스의 loadUserByUsername() 메소드를 오버라이딩. 로그인할 유저의 email을 파라미터로 받음.
        Member member = memberRepository.findByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if(member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()                           // UserDetail을 구현하고 있는 User 객체를 반환해줌. User 객체를 생성하기 위해 생성자로 회원의 이메일, 비밀번호, role을 파라미터로 넘겨줌.
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
