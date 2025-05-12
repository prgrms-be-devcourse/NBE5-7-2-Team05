package io.powerrangers.backend.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class AuthTestController {
    @GetMapping("/security-endpoint")
    public String securityEndpoint() {
        return "This is a security endpoint!";
    }

    @GetMapping("/url")
    public String testUrl() {
        return "This is a public endpoint";
    }
}
