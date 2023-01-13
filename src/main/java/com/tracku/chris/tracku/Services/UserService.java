package com.tracku.chris.tracku.Services;
import com.tracku.chris.tracku.Entities.User.Role;
import com.tracku.chris.tracku.Entities.User.User;
import com.tracku.chris.tracku.Interfaces.User.IUserService;
import com.tracku.chris.tracku.Repositories.UserRepository;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserAlreadyExistsException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserNotFoundException;
import com.tracku.chris.tracku.Utils.CustomExceptions.UserUnauthorizedException;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.AuthRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.RegisterRequest;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.UpdateNameRequest;
import com.tracku.chris.tracku.Utils.CustomResponses.AuthResponse;
import com.tracku.chris.tracku.Utils.CustomResponses.RegistrationResponse;
import com.tracku.chris.tracku.Utils.CustomResponses.UpdateNameResponse;
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
        Optional<User> user = userRepo.findByEmail(request.getEmail().strip());

        if(user.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User newUser = User.builder()
            .fullName(request.getFullName().strip())
            .email(request.getEmail().strip())
            .role(Role.USER)
            .userPassword(passwordEncoder.encode(request.getPassword().strip()))
            .build();

        userRepo.save(newUser);

        return RegistrationResponse.builder()
                .fullName(request.getFullName().strip())
                .email(request.getEmail().strip())
                .created_at(LocalDateTime.now())
                .build();
    }

    @Override
    public AuthResponse signInUser(AuthRequest request){
        String requestEmail = request.getEmail().strip();
        String requestPassword = request.getPassword().strip();
        Optional<User> user = userRepo.findByEmail(requestEmail);

        if(user.isEmpty()) {
            throw new UserNotFoundException("User does not exists");
        }
        User _user = user.get();

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(requestEmail, requestPassword));
        } catch(RuntimeException ex) {
            throw new UserUnauthorizedException("User is not authorized. Please, check your credentials");
        }

        String token = jwtService.createToken(_user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public UpdateNameResponse updateUsername(UpdateNameRequest request) {
        Optional<User> user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if(user.isEmpty()) {
            throw new UserNotFoundException("User does not exists");
        }

        String newName = request.getNewFullName().strip();
        User _user = user.get();

        _user.setFullName(newName);
        User updatedUser = userRepo.save(_user);
        System.out.println(updatedUser);
        return UpdateNameResponse.builder()
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .build();
    }
}