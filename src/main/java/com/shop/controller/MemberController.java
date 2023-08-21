package com.shop.controller;

import com.shop.config.auth.PrincipalDetails;
import com.shop.dto.MailDto;
import com.shop.dto.MemberFormDto;
import com.shop.dto.MemberUpdateDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import com.shop.service.MailService;
import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

import static com.shop.constant.Role.*;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    private final MailService mailService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")                 // 회원가입 페이지로 이동할 수 있도록 메소드 작성
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @GetMapping(value = "/select")
    public String memberSelect(Model model) {
        return "member/memberTypeSelectForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto,         // 검증하려는 객체의 앞에 @Valid 어노테이션을 선언하고, 파라미터로 bindingResult 객체 추가
                            BindingResult bindingResult, Model model) { // 검사 후 결과는 bindingResult에 담아줌.

        if(bindingResult.hasErrors()) {         // 에러가 있다면 회원 가입 페이지로 이동
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());     // 회원 가입 시 중복 회원 가입 예외가 발생하면 에러 메시지를 뷰로 전달
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/newAdmin")                 // 회원가입 페이지로 이동할 수 있도록 메소드 작성
    public String adminMemberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/newAdmin")
    public String newAdminMember(@Valid MemberFormDto memberFormDto,         // 검증하려는 객체의 앞에 @Valid 어노테이션을 선언하고, 파라미터로 bindingResult 객체 추가
                            BindingResult bindingResult, Model model) {      // 검사 후 결과는 bindingResult에 담아줌.

        if(bindingResult.hasErrors()) {         // 에러가 있다면 회원 가입 페이지로 이동
            return "member/memberForm";
        }

        try {
            Member member = Member.createAdminMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());     // 회원 가입 시 중복 회원 가입 예외가 발생하면 에러 메시지를 뷰로 전달
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }


    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    // 회원 아이디 찾기
    @RequestMapping(value = "/findId", method = RequestMethod.POST)
    @ResponseBody
    public String findId(@RequestParam("memberEmail") String memberEmail) {
        String email = String.valueOf(memberRepository.findByEmail(memberEmail));
        System.out.println("회원 이메일 = " + email);
        if(email == null) {
            return null;
        } else {
            return email;
        }
    }

    // 회원 비밀번호 찾기
    @GetMapping(value = "/findMember")
    public String findMember(Model model) {
        return "/member/findMemberForm";
    }

    // 비밀번호 찾기 시, 임시 비밀번호 담긴 이메일 보내기
    @Transactional
    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam("memberEmail") String memberEmail){
        MailDto dto = mailService.createMailAndChangePassword(memberEmail);
        mailService.mailSend(dto);

        return "/member/memberLoginForm";
    }

    // !!!! OAuth로 로그인 시 이 방식대로 하면 CastException 발생함
    @GetMapping("/form/loginInfo")
    @ResponseBody
    public String formLoginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String member = principal.getName();
        System.out.println(member);

        String user1 = principalDetails.getName();
        System.out.println(user1);

        return member.toString();
    }
    
    @GetMapping("/oauth/loginInfo")
    @ResponseBody
    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        // PrincipalOauth2UserService의 getAttributes내용과 같음

        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();
        // attributes == attributes1

        return attributes.toString();     //세션에 담긴 user가져올 수 있음
    }

    /* 회원정보 조회 */
    @GetMapping("/myInfo")
    public String memberInfo(Principal principal, ModelMap modelMap, Member member){
        String loginId = principal.getName();
        Member memberId = memberRepository.findByEmail(loginId);
        modelMap.addAttribute("member", memberId);

        if (memberId.getRole() == USER ) {
            System.out.println("USER LOGIN");
            return "mypage/FormMemberMyInfo";
        }
        if (memberId.getRole() == ADMIN) {
            System.out.println("ADMIN LOGIN");
            return "mypage/FormMemberMyInfo";
        }
        if (memberId.getRole() == SOCIAL) {
            System.out.println("SOCIAL LOGIN");
            return "mypage/OAuthMemberMyInfo";
        }
        return "null";
    }

    /* 회원 수정하기 전 비밀번호 확인 */
    @GetMapping("/checkPwdForm")
    public String checkPwdView(){
        return "member/passwordCheckForm";
    }

    @GetMapping("/checkPwd")
    @ResponseBody
    public boolean checkPassword(Principal principal, Member member,
                                 @RequestParam String checkPassword,
                                 Model model){

        String loginId = principal.getName();

        Member memberId = memberRepository.findByEmail(loginId);
        System.out.println(memberId.getPassword());
        return memberService.checkPassword(memberId, checkPassword);
    }

    // 회원 정보 변경 폼
    @GetMapping(value = "/updateForm")
    public String updateMemberForm(Principal principal, Model model) {
        String loginId = principal.getName();
        Member memberId = memberRepository.findByEmail(loginId);
        model.addAttribute("member", memberId);

        return "/settings/memberUpdateForm";
    }

    // 회원 정보 변경
    @PostMapping(value = "/updateForm")
    public String updateMember(@Valid MemberUpdateDto memberUpdateDto, Principal principal, Model model) {
        model.addAttribute("member", memberUpdateDto);
        memberService.updateMember(memberUpdateDto);
        return "redirect:/members/myInfo";
    }


}