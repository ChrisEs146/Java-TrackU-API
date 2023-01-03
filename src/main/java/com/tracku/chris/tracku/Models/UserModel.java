package com.tracku.chris.tracku.Models;
import com.tracku.chris.tracku.CustomValidators.User.ComparePasswords;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ComparePasswords
public class UserModel {
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
    private String userImage;
}
