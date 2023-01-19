package com.tracku.chris.tracku.Services;
import com.tracku.chris.tracku.Entities.User.UserEntity;
import com.tracku.chris.tracku.Interfaces.User.IUserService;
import com.tracku.chris.tracku.Repositories.UserRepository;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.*;
import com.tracku.chris.tracku.Utils.CustomResponses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Override
    public RegistrationResponse registerUser(RegisterRequest request){
        Optional<UserEntity> user = userRepo.findByEmail(request.getEmail().strip());

        if(user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserEntity newUser = UserEntity.builder()
            .full_name(request.getFullName().strip())
            .email(request.getEmail().strip())
            .user_password(passwordEncoder.encode(request.getPassword().strip()))
            .build();

        UserEntity createdUser = userRepo.save(newUser);

        return RegistrationResponse.builder()
                .id(createdUser.getUser_Id())
                .fullName(createdUser.getFull_name())
                .email(createdUser.getEmail())
                .created_at(LocalDateTime.now())
                .build();
    }

    @Override
    public AuthResponse signInUser(AuthRequest request){
        String requestEmail = request.getEmail().strip();
        String requestPassword = request.getPassword().strip();
        Optional<UserEntity> user = userRepo.findByEmail(requestEmail);

        if(user.isEmpty()) {
            throw new UserNotFoundException("User does not exists");
        }
        UserEntity _user = user.get();

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(requestEmail, requestPassword));
        } catch(RuntimeException ex) {
            throw new UserUnauthorizedException("User is not authorized. Please, check your credentials");
        }

        String token = jwtService.createToken(_user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public UpdateNameResponse updateUsername(UpdateNameRequest request) {
        UserEntity user = userRepo.findByEmail(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName())
                .get();

        String newName = request.getNewFullName().strip();

        user.setFull_name(newName);
        UserEntity updatedUser = userRepo.save(user);
        return UpdateNameResponse.builder()
                .id(updatedUser.getUser_Id())
                .fullName(updatedUser.getFull_name())
                .email(updatedUser.getEmail())
                .build();
    }

    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest request) {
        UserEntity user = userRepo.findByEmail(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName())
                .get();

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getUser_password())) {
            throw new UserUnauthorizedException("Not authorized. Check your credentials");
        }

        user.setUser_password(passwordEncoder.encode(request.getNewPassword().strip()));
        userRepo.save(user);
        return UpdatePasswordResponse.builder().build();
    }

    @Override
    public UserInfoResponse getUserInfo() {
        UserEntity user = userRepo.findByEmail(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName())
                .get();

        return UserInfoResponse.builder()
                .id(user.getUser_Id())
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .build();
    }

    @Override
    public DeleteUserResponse deleteUser(DeleteUserRequest request) {
        UserEntity user = userRepo.findByEmail(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName())
                .get();

        userRepo.delete(user);
        return DeleteUserResponse.builder()
                .id(user.getUser_Id())
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .build();
    }
}
