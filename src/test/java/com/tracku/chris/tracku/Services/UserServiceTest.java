package com.tracku.chris.tracku.Services;

import com.tracku.chris.tracku.Entities.User.UserEntity;
import com.tracku.chris.tracku.Repositories.UserRepository;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.AuthRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.RegisterRequest;
import com.tracku.chris.tracku.Utils.CustomResponses.RegistrationResponse;
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
                .hasMessageContaining("User already exists");

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
                .hasMessageContaining("User does not exists");
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

        Assertions.assertThatThrownBy(() -> userService.signInUser(request))
                .isInstanceOf(UserUnauthorizedException.class)
                .hasMessageContaining("User is not authorized. Please, check your credentials");
        verify(jwtService, never()).createToken(any());
    }

    @Test
    @Disabled
    public void updateUsername() {
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