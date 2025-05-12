package io.powerrangers.backend.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {
    @GetMapping("/security-endpoint")
    public String securityEndpoint() {
        return "This is a security endpoint!";
    }

    @GetMapping("/test/url")
    public String testUrl() {
        return "This is a public endpoint";
    }
}
