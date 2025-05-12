package io.powerrangers.backend.config;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.powerrangers.backend.dto.Role;
import io.powerrangers.backend.dto.TokenBody;
import io.powerrangers.backend.dto.UserDetails;
import io.powerrangers.backend.service.CustomOauth2UserService;
import io.powerrangers.backend.service.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthTestController.class)
@ExtendWith(MockitoExtension.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@MockitoBean(types = JpaMetamodelMappingContext.class)
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    JwtProvider jwtProvider;

    @MockitoBean
    CustomOauth2UserService oauth2UserService;

    @MockitoBean
    Oauth2SuccessHandler oauth2SuccessHandler;

    @Test
    @DisplayName("필터 내 토큰 검증 성공")
    void testValidToken() throws Exception {
        String token = "valid token";
        UserDetails userDetails = UserDetails.builder()
                .name("name")
                .email("email")
                .providerId("kakao")
                .profileImage("profile")
                .attributes(null)
                .build();
        TokenBody tokenBody = TokenBody.builder()
                .userId(-1L)
                .role(Role.USER.name())
                .build();
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.parseToken(token)).thenReturn(tokenBody);
        when(oauth2UserService.getUserDetails(tokenBody.getUserId())).thenReturn(userDetails);

        mockMvc.perform(get("/test/security-endpoint")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk());
    }
}