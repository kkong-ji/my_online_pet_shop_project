package com.shop.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/thymeleaf")         // 클라이언트의 요청에 따라 어떤 컨트롤러가 처리할 지 매핑
public class ThymeleafExController {

    @GetMapping(value = "/ex01")
    public String thymeleafExample01(Model model) {
        model.addAttribute("data", "타임리프 예제 입니다.");     // model 객체를 이용. 뷰에 전달할 데이터를 key, value로 넣어줌
        return "thymeleafEx/thymeleafEx01";
    }
}
