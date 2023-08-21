package com.shop.service;

import com.shop.dto.MailDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "daengdaengworld@gmail.com";
    private static int number;
    private final MemberRepository memberRepository;

    // 이메일 인증 난수 발생
    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[댕댕월드 이메일 인증 번호입니다.]");
            String body = "";
            body += "<h1>" + "댕댕월드 메일 인증" + "</h1>";
            body += "<h3>" + "댕댕월드에 오신 것을 환영합니다!" + "</h3>";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "상단의 인증 번호를 입력창에 입력해주세요. 감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }


    // 임시 비밀번호 생성
    public static String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    // 메일 내용을 생성하고 임시 비밀번호로 회원 비밀번호를 변경
    public MailDto createMailAndChangePassword(String memberEmail) {
        String str = getTempPassword();
        MailDto dto = new MailDto();
        dto.setAddress(memberEmail);
        dto.setTitle("댕댕월드 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. 댕댕월드 임시비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
                + str + " 입니다." + "로그인 후에 비밀번호를 변경해주세요!");
        updatePassword(str, memberEmail);
        return dto;
    }

    public void mailSend(MailDto mailDto) {
        System.out.println("전송 완료!");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());
        message.setFrom("wisejohn950330@gmail.com");
        message.setReplyTo("wisejohn950330@gmail.com");
        System.out.println("message"+message);
        javaMailSender.send(message);
    }

    //임시 비밀번호로 업데이트
    public boolean updatePassword(String str, String email){
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodePw = encoder.encode(str);
            Member member = memberRepository.findByEmail(email);
            member.updateOriginalPassword(str);
            member.updatePassword(encodePw);
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int sendMail(String mail){

        MimeMessage message = CreateMail(mail);

        javaMailSender.send(message);

        return number;
    }

}