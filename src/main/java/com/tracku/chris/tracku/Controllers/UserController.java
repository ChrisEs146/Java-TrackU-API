package com.tracku.chris.tracku.Controllers;
import com.tracku.chris.tracku.Services.UserService;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.*;
import com.tracku.chris.tracku.Utils.CustomResponses.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegisterRequest request){
        RegistrationResponse createdUser = userService.registerUser(request);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>signInUser(@Valid @RequestBody AuthRequest request){
        AuthResponse userToken = userService.signInUser(request);
        return ResponseEntity.ok(userToken);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<UpdateNameResponse> updateUsername(@Valid @RequestBody UpdateNameRequest request) {
        UpdateNameResponse response = userService.updateUsername(request);
        return ResponseEntity.ok(response);
    }
}
