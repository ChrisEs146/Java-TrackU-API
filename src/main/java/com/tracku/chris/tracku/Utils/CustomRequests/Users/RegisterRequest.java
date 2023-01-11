package com.tracku.chris.tracku.Utils.CustomRequests.Users;
import com.tracku.chris.tracku.Utils.CustomValidators.User.ComparePasswords;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComparePasswords
public class RegisterRequest {
    @NotBlank(message = "Full name can't be empty")
    @Size(min = 4, message = "Full name should be at least 4 chars long")
    private String fullName;
    @Email(message="Provide a valid email")
    @NotBlank(message = "Email can't be empty")
    private String email;
    @NotBlank(message="Password can't be empty")
    @Pattern(regexp = "^(?=\\S)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#%^&*])[a-zA-Z0-9!@#%^&*]{8,20}$", message = "Password should be 8-20chars, contain numbers, lowercase and uppercase letters and '!@#%^&*' special chars")
    private String password;
    @NotBlank(message="Confirm password can't be empty")
    private String confirmPassword;
}
