package io.powerrangers.backend.controller;

import io.powerrangers.backend.dto.UserGetProfileResponseDto;
import io.powerrangers.backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("/loginPage")
    public String loginPage(HttpSession session, Model model) {
        String errorMessage = (String) session.getAttribute("error");
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            session.removeAttribute("error");
        }
        return "loginpage";
    }

    // 🟢 마이페이지 조회 (view: mypage.html)
    @GetMapping("/mypage")
    public String showMypage() {
        return "mypage"; // templates/mypage.html
    }

    // 🟢 마이페이지 수정 폼 (view: updatemypage.html)
    @GetMapping("/mypage/update")
    public String showUpdateMypage() {
        return "updatemypage"; // templates/updatemypage.html
    }
}
