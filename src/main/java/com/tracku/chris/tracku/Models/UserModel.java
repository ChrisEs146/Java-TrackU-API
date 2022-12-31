package com.tracku.chris.tracku.Models;
import com.tracku.chris.tracku.CustomValidators.User.ValidateConfirmPassword;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidateConfirmPassword(message = "Passwords do not match")
public class UserModel {
    @NotBlank(message = "Please, provide a full name")
    private String fullName;
    @Email(message="Please, provide a valid email")
    @NotBlank(message = "Please, provide an email")
    private String email;
    @NotBlank(message="Please, provide a password")
    @Pattern(regexp = "^(?=\\S)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#%^&*])[a-zA-Z0-9!@#%^&*]{8,20}$", message = "It should be 8-20chars, contain numbers, lowercase and uppercase letters and '!@#%^&*' special chars")
    private String password;
    @NotBlank(message="Please, provide a password")
    private String confirmPassword;
    private String userImage;
}
