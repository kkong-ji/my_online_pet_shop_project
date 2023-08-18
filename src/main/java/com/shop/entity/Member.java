package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import com.shop.service.MailService;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;
import java.util.Collection;

@Builder
@Entity
@Table(name="member")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {                     // 회원 정보 저장 엔티티

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;                                   // 회원은 이메일을 통해 유일하게 구분하므로 unique 속성 지정

    private String originalpassword;

    private String password;

    private String picture;

    private String address;

    private String streetaddress;

    private String detailaddress;

    @Enumerated(EnumType.STRING)                            // enum의 순서가 바뀌지 않도록 하기위해 enumType을 String으로 지정
    private Role role;

    private String provider;   // oauth2를 이용할 경우 어떤 플랫폼을 이용하는지
    private String providerId; // oauth2를 이용할 경우 아이디값

    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder) {

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getZipcode());
        member.setDetailaddress(memberFormDto.getDetailadr());
        member.setStreetaddress(memberFormDto.getStreetadr());
        member.setOriginalpassword(memberFormDto.getPassword());
        String password = passwordEncoder.encode(memberFormDto.getPassword());      // 스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호 암호화
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }

    public static Member createAdminMember(MemberFormDto memberFormDto,
                                           PasswordEncoder passwordEncoder) {

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getZipcode());
        member.setDetailaddress(memberFormDto.getDetailadr());
        member.setStreetaddress(memberFormDto.getStreetadr());
        member.setOriginalpassword(memberFormDto.getPassword());
        String password = passwordEncoder.encode(memberFormDto.getPassword());      // 스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호 암호화
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    public Member updateModifiedDate() {
        this.onPreUpdate();
        return this;
    }

    @Builder(builderClassName = "UserDetailRegister", builderMethodName = "userDetailRegister")
    public Member(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public Member(String name, String password, String email, Role role, String provider, String providerId) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.name = name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * 회원수정 메소드
     */
    public void updateUsername(String name) { this.name = name; }

    public void updatePassword(String password) {
        this.password = password;
      }

    public void updateAddress(String address) { this.address = address; }

    public void updateStreetAddress(String streetaddress) { this.streetaddress = streetaddress; }

    public void updateDetailAddress(String detailaddress) { this.detailaddress = detailaddress; }

}