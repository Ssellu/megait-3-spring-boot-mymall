package com.megait.mymall.controller;

import com.megait.mymall.domain.Member;
import com.megait.mymall.repository.MemberRepository;
import com.megait.mymall.validation.SignUpForm;
import com.megait.mymall.service.MemberService;
import com.megait.mymall.validation.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.Validator;
import java.security.Principal;
import java.util.Optional;

@Controller
@Slf4j // log 변수
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @InitBinder("signUpForm")
    protected void initBinder(WebDataBinder binder){
        binder.addValidators(new SignUpFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(@AuthenticationMember Member member, Model model) {
        if(member != null){
            model.addAttribute(member);
        }
        return "index";
    }

    @GetMapping("/signup")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        // 유효성 검사 시작. - initBinder() 가 실행됨.
        if(errors.hasErrors()){
            log.error("errors : {}", errors.getAllErrors());
            return "member/signup"; // "redirect:/signup"
        }
        log.info("올바른 회원 정보.");

        // 회원가입 서비스 실행
        Member member = memberService.processNewMember(signUpForm);

        // 로그인했다고 처리
        memberService.login(member);

        return "redirect:/"; // "/"로 리다이렉트
    }

    @GetMapping("/email-check-token")
    @Transactional
    public String emailCheckToken(String token, String email, Model model){
        Optional<Member> optional = memberRepository.findByEmail(email);
        if(optional.isEmpty()){
//            log.info("Email does not exist.");
//            return "redirect:/";
            model.addAttribute("error", "wrong.email");
            return "member/checked-email";
        }

        Member member = optional.get();
        if(!member.isValidToken(token)){
//            log.info("Token does not match.");
//            return "redirect:/";
            model.addAttribute("error", "wrong.token");
            return "member/checked-email";
        }
        // log.info("Success!");
        model.addAttribute("email", member.getEmail());
        member.completeSignUp();
        return "member/checked-email";
    }

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

}
