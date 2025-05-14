//package io.powerrangers.backend.service;
//
//import io.powerrangers.backend.dao.UserRepository;
//import io.powerrangers.backend.dto.*;
//import io.powerrangers.backend.entity.RefreshToken;
//import io.powerrangers.backend.entity.User;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTests {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    @Mock
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @Mock
//    private TokenRepository refreshTokenRepositoryAdapter;
//
//    @Mock
//    private JwtProvider jwtProvider;
//
//
//    @Test
//    @DisplayName("닉네임 중복 없을 경우 확인 테스트")
//    void checkNicknameDuplication_noExist_test() {
//        //given
//        String nickname = "testNickname";
//        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());
//
//        //when
//        boolean result = userService.checkNicknameDuplication(nickname);
//
//        //then
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 있을 경우 확인 테스트")
//    void checkNicknameDuplication_Exist_test() throws Exception{
//        //given
//        String nickname = "testNickName";
//        User mockUser = mock(User.class);
//        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(mockUser));
//
//        //when
//        boolean result = userService.checkNicknameDuplication(nickname);
//
//        //then
//        assertThat(result).isTrue();
//
//
//    }
//
//    @Test
//    @DisplayName("UserProfile을 정상적으로 가져왔을 경우 테스트")
//    void getUserProfile_success_test() {
//        // given
//        User user = new User("testUser1","imagePath","provider","providerId","test@email.com");
//        user.setIntro("test123");
//        given(userRepository.findById(1L)).willReturn(Optional.of(user));
//
//        // when
//        UserGetProfileResponseDto userProfile = userService.getUserProfile(1L);
//
//        // then
//        assertThat(userProfile).isNotNull();
//        assertThat(userProfile.getNickname()).isEqualTo("testUser1");
//        assertThat(userProfile.getIntro()).isEqualTo("test123");
//        assertThat(userProfile.getProfileImage()).isEqualTo("imagePath");
//    }
//
//    @Test
//    @DisplayName("UserProfile을 가져오지 못할 경우 테스트")
//    void getUserProfile_fail_test() {
//        // given
//        given(userRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> userService.getUserProfile(1L))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("존재하지 않는 사용자입니다.");
//    }
//
//    @Test
//    @DisplayName("검색하여 UserProfile을 제대로 가져온 경우 테스트")
//    void searchUserProfile_success_test() {
//        // given
//        User user = new User("testUser1","imagePath","provider","providerId","test@email.com");
//        user.setIntro("test123");
//        given(userRepository.findByNickname("testUser1")).willReturn(Optional.of(user));
//
//        // when
//        UserGetProfileResponseDto userSearch = userService.searchUserProfile("testUser1");
//
//        // then
//        assertThat(userSearch).isNotNull();
//        assertThat(userSearch.getIntro()).isEqualTo("test123");
//        assertThat(userSearch.getProfileImage()).isEqualTo("imagePath");
//        assertThat(userSearch.getNickname()).isEqualTo("testUser1");
//    }
//
//    @Test
//    @DisplayName("검색하여 UserProfile을 제대로 가져오지 못한 경우 테스트")
//    void searchUserProfile_fail_test() {
//        // given
//        given(userRepository.findByNickname("testUser1")).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> userService.searchUserProfile("testUser1"))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("존재하지 않는 사용자입니다.");
//    }
//
//    @Test
//    @DisplayName("UserProfile Update 성공 테스트")
//    void updateUserProfile_success_test() {
//        // given
//        User user = new User("oldNickname", "oldImage", "provider", "providerId", "test@email.com");
//        user.setIntro("oldIntro");
//
//        UserUpdateProfileRequestDto request = UserUpdateProfileRequestDto.builder()
//                .intro("newIntro")
//                .nickname("newNickname")
//                .profileImage("newImage")
//                .build();
//
//        given(userRepository.findById(1L)).willReturn(Optional.of(user));
//        given(userRepository.findByNickname("newNickname")).willReturn(Optional.empty());
//        // when
//        userService.updateUserProfile(1L, request);
//
//        // then
//        assertThat(user.getNickname()).isEqualTo("newNickname");
//        assertThat(user.getIntro()).isEqualTo("newIntro");
//        assertThat(user.getProfileImage()).isEqualTo("newImage");
//
//    }
//
//    @Test
//    @DisplayName("UserProfile Update 닉네임을 변경하지 않았을 때 성공 테스트")
//    void updateUserProfile_success_notChangedNickname_test() throws Exception{
//        // given
//        User user = User.builder()
//                .nickname("oldNickname")
//                .profileImage("oldImage")
//                .provider("provider")
//                .providerId("providerId")
//                .email("test@email.com")
//                .build();
//        user.setIntro("oldIntro");
//
//        UserUpdateProfileRequestDto request = UserUpdateProfileRequestDto.builder()
//                .nickname("oldNickname")
//                .intro("newIntro")
//                .profileImage("newImage")
//                .build();
//
//        given(userRepository.findById(1L)).willReturn(Optional.of(user));
//
//        // when
//        userService.updateUserProfile(1L, request);
//
//        // then
//        assertThat(user.getNickname()).isEqualTo("oldNickname");
//        assertThat(user.getIntro()).isEqualTo("newIntro");
//        assertThat(user.getProfileImage()).isEqualTo("newImage");
//
//    }
//
//    @Test
//    @DisplayName("UserProfile Update 존재하지 않는 사용자일 경우 예외 발생 테스트")
//    void updateUserProfile_fail_userNotFound_test(){
//        // given
//        Long invalidUserId = 999L;
//
//        UserUpdateProfileRequestDto request = UserUpdateProfileRequestDto.builder()
//                .nickname("newNickName")
//                .intro("newIntro")
//                .profileImage("newImagePath")
//                .build();
//
//        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> userService.updateUserProfile(invalidUserId,request))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("존재하지 않는 사용자입니다.");
//
//    }
//
//    @Test
//    @DisplayName("UserProfile Update 다른 유저와 닉네임이 중복될 경우 예외 발생 테스트")
//    void updateUserProfile_fail_nicknameDuplicate_test() throws Exception{
//        // given
//        Long userId = 1L;
//        String duplicateNickname = "existingNickname";
//
//        User user = new User("testUser1","imagePath","provider","providerId","test@email.com");
//        user.setIntro("test123");
//
//        UserUpdateProfileRequestDto request = UserUpdateProfileRequestDto.builder()
//                .intro("existIntro")
//                .profileImage("existImagePath")
//                .nickname(duplicateNickname)
//                .build();
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(userRepository.findByNickname(duplicateNickname)).willReturn(Optional.of(user));
//
//        // when & then
//        assertThatThrownBy(() -> userService.updateUserProfile(1L, request))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("닉네임이 중복됩니다.");
//
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴 성공 테스트")
//    void cancelAccount_success_test() {
//        // given
//        Long userId = 1L;
//
//        // when
//        userService.cancelAccount(userId);
//
//        // then
//        verify(userRepository, times(1)).deleteById(userId);
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 회원 탈퇴 실패 테스트")
//    void cancelAccount_fail_NotExistUser_test() throws Exception{
//        // given
//        Long userId = 999L;
//        given(userRepository.findById(userId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> userService.cancelAccount(userId))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("존재하지 않는 사용자입니다.");
//
//        verify(userRepository, never()).deleteById(anyLong());
//
//    }
//
//    @Test
//    @DisplayName("로그아웃 성공 테스트")
//    void logout_success_test() throws Exception{
//        // given
//        String refreshTokenValue = "refreshTokenValue";
//        User user = User.builder()
//                .provider("provider")
//                .nickname("nickname")
//                .providerId("providerId")
//                .email("email")
//                .profileImage("profileImage")
//                .build();
//        user.setId(1L);
//        user.setIntro("intro");
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                .user(user)
//                .refreshToken(refreshTokenValue)
//                .build();
//
//        UserDetails userDetails = UserDetails.from(user);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, List.of() );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        given(refreshTokenRepositoryAdapter.tokenBlackList(refreshTokenValue)).willReturn(false);
//        given(refreshTokenRepositoryAdapter.findValidRefreshToken(user.getId()))
//                .willReturn(Optional.of(refreshToken));
//        given(refreshTokenRepositoryAdapter.addBlackList(any(RefreshToken.class))).willReturn(null);
//
//        // when
//        userService.logout();
//
//        // then
//        verify(refreshTokenRepositoryAdapter).addBlackList(any(RefreshToken.class));
//
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    @DisplayName("access token 재발급 성공 테스트")
//    void reissue_success_test () throws Exception{
//        // given
//        String bearerToken = "Bearer valid.refresh.token";
//        String pureToken = "valid.refresh.token";
//
//        TokenBody tokenBody = TokenBody.builder()
//                .userId(1L)
//                .role("USER")
//                .build();
//
//        RefreshToken mockRefreshToken = RefreshToken.builder()
//                .refreshToken(pureToken)
//                .build();
//
//        given(jwtProvider.validateToken(pureToken)).willReturn(true);
//        given(jwtProvider.parseToken(pureToken)).willReturn(tokenBody);
//        given(refreshTokenRepositoryAdapter.findValidRefreshToken(1L))
//                .willReturn(Optional.of(mockRefreshToken));
//        given(jwtProvider.issueAccessToken(1L, Role.USER)).willReturn("new.access.token");
//
//        // when
//        String accessToken = userService.reissueAccessToken(bearerToken);
//        // then
//        assertEquals("new.access.token", accessToken);
//
//
//    }
//}