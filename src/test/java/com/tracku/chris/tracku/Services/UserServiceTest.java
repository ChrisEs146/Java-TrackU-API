package com.tracku.chris.tracku.Services;

import com.tracku.chris.tracku.Entities.User.CustomUserDetails;
import com.tracku.chris.tracku.Entities.User.UserEntity;
import com.tracku.chris.tracku.Repositories.UserRepository;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.*;
import com.tracku.chris.tracku.Utils.CustomResponses.*;
import com.tracku.chris.tracku.Utils.ErrorMessages.UserErrorMsg;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = UserEntity.builder()
                .user_Id(1)
                .full_name("Arthur Morgan")
                .email("arthur@email.com")
                .user_password("password44#%")
                .created_at(LocalDateTime.now())
                .build();
    }

    @Test
    public void userService_RegisterUser_RegistersUser() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .password(user.getUser_password())
                .confirmPassword(user.getUser_password())
                .build();

        when(userRepo.save(any(UserEntity.class))).thenReturn(user);
        userService.registerUser(request);

        ArgumentCaptor<UserEntity> requestArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(requestArgumentCaptor.capture());
        UserEntity capturedUser = requestArgumentCaptor.getValue();

        Assertions.assertThat(capturedUser).isNotNull();
        Assertions.assertThat(capturedUser.getFull_name()).isEqualTo(user.getFull_name());
        Assertions.assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void userService_RegisterUser_ThrowsUserAlreadyExistsException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .password(user.getUser_password())
                .confirmPassword(user.getUser_password())
                .build();

        given(userRepo.findByEmail(request.getEmail())).willReturn(Optional.of(user));
        Assertions.assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining(UserErrorMsg.USER_EXISTS.label);

        verify(userRepo, never()).save(any());
    }

    @Test
    public void userService_RegisterUser_ReturnsRegistrationResponse() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .password(user.getUser_password())
                .confirmPassword(user.getUser_password())
                .build();

        when(userRepo.save(any(UserEntity.class))).thenReturn(user);
        RegistrationResponse testResponse = userService.registerUser(request);

        Assertions.assertThat(testResponse).isNotNull();
        Assertions.assertThat(testResponse.getId()).isGreaterThan(0);
        Assertions.assertThat(testResponse.getFullName()).isEqualTo(request.getFullName());
        Assertions.assertThat(testResponse.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    public void userService_SignInUser_ThrowsUserNotFoundException() {
        AuthRequest request = AuthRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();
        AuthenticationManager authManager = mock(AuthenticationManager.class);

        given(userRepo.findByEmail(request.getEmail())).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.signInUser(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(UserErrorMsg.NOT_FOUND.label);
        verify(authManager, never()).authenticate(any());
        verify(jwtService, never()).createToken(any());
    }

    @Test
    public void userService_SignInUser_ThrowsUserUnauthorizedException() {
        AuthRequest request = AuthRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(new RuntimeException("Bad credentials"));

         Assertions.assertThatThrownBy(() -> userService.signInUser(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.INVALID_CREDENTIALS.label);
        verify(jwtService, never()).createToken(any());
    }

    @Test
    public void userService_SignInUser_ReturnsAuthResponse() {
        AuthRequest request = AuthRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();
        String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJVc2VybmFtZSI6IkFydGh1ciBNb3JnYW4iLCJJZCI6IjEifQ.YK497eKwf7o02dJ4aOf1imhJC1iZfp2K_3qiizfe-As";
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(true);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        given(authManager.authenticate(any())).willReturn(auth);
        when(jwtService.createToken(anyString())).thenReturn(testToken);
        AuthResponse testResponse = userService.signInUser(request);

        Assertions.assertThat(testResponse).isNotNull();
        Assertions.assertThat(testResponse.getToken()).isInstanceOf(String.class);
        Assertions.assertThat(testResponse.getToken()).isEqualTo(testToken);
    }

    @Test
    public void userService_UpdateUsername_ThrowsUserUnauthorizedException() {
        UpdateNameRequest request = UpdateNameRequest.builder()
                .newFullName("Arthur Salas")
                .build();
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThatThrownBy(() -> userService.updateUsername(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.UNAUTHORIZED.label);
        verify(userRepo, never()).save(any());
    }

    @Test
    public void userService_UpdateUsername_SavesUser() {
        UpdateNameRequest request = UpdateNameRequest.builder()
                .newFullName("Arthur Salas")
                .build();
        UserEntity updatedUserTest = user;
        updatedUserTest.setFull_name(request.getNewFullName());
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(userRepo.save(any(UserEntity.class))).thenReturn(updatedUserTest);
        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        userService.updateUsername(request);

        ArgumentCaptor<UserEntity> requestArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(requestArgumentCaptor.capture());
        UserEntity capturedUser = requestArgumentCaptor.getValue();

        Assertions.assertThat(capturedUser).isNotNull();
        Assertions.assertThat(capturedUser.getFull_name()).isEqualTo(updatedUserTest.getFull_name());
        Assertions.assertThat(capturedUser.getEmail()).isEqualTo(updatedUserTest.getEmail());
    }

    @Test
    public void userService_UpdateUsername_ReturnsUpdateNameResponse() {
        UpdateNameRequest request = UpdateNameRequest.builder()
                .newFullName("Arthur Salas")
                .build();
        user.setFull_name(request.getNewFullName());
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userRepo.save(any(UserEntity.class))).thenReturn(user);
        UpdateNameResponse response = userService.updateUsername(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isGreaterThan(0);
        Assertions.assertThat(response.getFullName()).isEqualTo(user.getFull_name());
    }

    @Test
    public void userService_UpdatePassword_ThrowsUserUnauthorizedException() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword(user.getUser_password())
                .newPassword("superStrongpass22%@")
                .confirmPassword("superStrongpass22%@")
                .build();
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        Assertions.assertThatThrownBy(() -> userService.updatePassword(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.UNAUTHORIZED.label);
        verify(userRepo, never()).save(any());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    public void userService_UpdatePassword_ThrowsException_MatchingPasswords() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword(user.getUser_password())
                .newPassword("superStrongpass22%@")
                .confirmPassword("superStrongpass22%@")
                .build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        Assertions.assertThatThrownBy(() -> userService.updatePassword(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.INVALID_CURRENT_PASSWORD.label);

        verify(userRepo, never()).save(any());
    }

    @Test
    public void userService_UpdatePassword_SavesUser() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword(user.getUser_password())
                .newPassword("superStrongpass22%@")
                .confirmPassword("superStrongpass22%@")
                .build();
        user.setUser_password(request.getNewPassword());
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        userService.updatePassword(request);
        ArgumentCaptor<UserEntity> requestArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(requestArgumentCaptor.capture());
        UserEntity capturedUser = requestArgumentCaptor.getValue();

        Assertions.assertThat(capturedUser).isNotNull();
        Assertions.assertThat(capturedUser.getFull_name()).isEqualTo(user.getFull_name());
        Assertions.assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(capturedUser.getUser_password()).isEqualTo(user.getUser_password());
    }

    @Test
    public void userService_UpdatePassword_ReturnsUpdatePasswordResponse() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword(user.getUser_password())
                .newPassword("superStrongpass22%@")
                .confirmPassword("superStrongpass22%@")
                .build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        UpdatePasswordResponse response = userService.updatePassword(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getMessage()).isEqualTo("Password updated successfully");
    }

    @Test
    public void userService_GetUserInfo_ThrowsUserUnauthorizedException() {
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        Assertions.assertThatThrownBy(() -> userService.getUserInfo())
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.UNAUTHORIZED.label);
    }

    @Test
    public void userService_GetUserInfo_ReturnsUserInfoResponse() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        UserInfoResponse response = userService.getUserInfo();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isGreaterThan(0);
        Assertions.assertThat(response.getFullName()).isEqualTo(user.getFull_name());
        Assertions.assertThat(response.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void userService_DeleteUser_ThrowsUserUnauthorizedException() {
        DeleteUserRequest request = DeleteUserRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();
        Authentication auth = mock(Authentication.class);
        auth.setAuthenticated(false);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        Assertions.assertThatThrownBy(() -> userService.deleteUser(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.UNAUTHORIZED.label);
    }

    @Test
    public void userService_DeleteUser_ThrowsException_WhenBadCredentials() {
        DeleteUserRequest request = DeleteUserRequest.builder()
                .email("roger@email.com")
                .password(user.getUser_password())
                .build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        Assertions.assertThatThrownBy(() -> userService.deleteUser(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining(UserErrorMsg.INVALID_CREDENTIALS.label);

        verify(userRepo, never()).delete(any());
    }

    @Test
    public void userService_DeleteUser_DeletesUser() {
        DeleteUserRequest request = DeleteUserRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);

        userService.deleteUser(request);
        ArgumentCaptor<UserEntity> requestArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).delete(requestArgumentCaptor.capture());
        UserEntity capturedUser = requestArgumentCaptor.getValue();

        Assertions.assertThat(capturedUser).isNotNull();
        Assertions.assertThat(capturedUser.getUser_Id()).isGreaterThan(0);
        Assertions.assertThat(capturedUser.getFull_name()).isEqualTo(user.getFull_name());
        Assertions.assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void userService_DeleteUser_ReturnsDeleteUserResponse() {
        DeleteUserRequest request = DeleteUserRequest.builder()
                .email(user.getEmail())
                .password(user.getUser_password())
                .build();
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);

        DeleteUserResponse response = userService.deleteUser(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isGreaterThan(0);
        Assertions.assertThat(response.getFullName()).isEqualTo(user.getFull_name());
        Assertions.assertThat(response.getEmail()).isEqualTo(user.getEmail());
    }
}