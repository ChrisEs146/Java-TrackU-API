package com.tracku.chris.tracku.Services;

import com.tracku.chris.tracku.Entities.User.CustomUserDetails;
import com.tracku.chris.tracku.Entities.User.UserEntity;
import com.tracku.chris.tracku.Repositories.UserRepository;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.AuthRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.RegisterRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.UpdateNameRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.UpdatePasswordRequest;
import com.tracku.chris.tracku.Utils.CustomResponses.AuthResponse;
import com.tracku.chris.tracku.Utils.CustomResponses.RegistrationResponse;
import com.tracku.chris.tracku.Utils.CustomResponses.UpdateNameResponse;
import com.tracku.chris.tracku.Utils.ErrorMessages.UserErrorMsg;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
        UserEntity updatedUserTest = user;
        updatedUserTest.setFull_name(request.getNewFullName());
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        auth.setAuthenticated(true);

        when(auth.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userRepo.save(any(UserEntity.class))).thenReturn(updatedUserTest);
        UpdateNameResponse response = userService.updateUsername(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isGreaterThan(0);
        Assertions.assertThat(response.getFullName()).isEqualTo(updatedUserTest.getFull_name());



    }

    @Test
    @Disabled
    public void updatePassword() {
    }

    @Test
    @Disabled
    public void getUserInfo() {
    }

    @Test
    @Disabled
    public void deleteUser() {
    }
}