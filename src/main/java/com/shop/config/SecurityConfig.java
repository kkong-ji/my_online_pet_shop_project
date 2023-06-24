package com.shop.config;

import com.shop.config.auth.PrincipalOauth2UserService;
import com.shop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")            // 로그인 페이지 URL
                .defaultSuccessUrl("/")                 // 로그인 성공 시 이동할 URL
                .usernameParameter("email")             // 로그인 시 사용할 파라미터 이름
                .failureUrl("/members/login/error")     // 로그인 실패 시 이동할 URL
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))    // 로그아웃 URL
                .logoutSuccessUrl("/")                  // 로그아웃 성공 시 이동할 URL
                .and()                                  // OAuth
                .oauth2Login()
                .defaultSuccessUrl("/")
                .userInfoEndpoint()                     // OAuth2 로그인 성공 후 가져올 설정들
                .userService(principalOauth2UserService);  // 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시
        ;

        http.authorizeRequests()                        // 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()    // 모든 사용자가 인증(로그인) 없이 해당 경로에 접근할 수 있도록 설정
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")      // /admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능하도록 설정
                .anyRequest().authenticated()           // 위에서 설정해준 경로를 제외한 나머지 경로들은 모두 인증을 요구하도록 설정
                .and()
                .csrf().ignoringAntMatchers("/mail/**")
        ;

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())         // 인증되지 않은 사용자가 리소스에 접근했을 때 수행되는 핸들러 등록
        ;

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

}