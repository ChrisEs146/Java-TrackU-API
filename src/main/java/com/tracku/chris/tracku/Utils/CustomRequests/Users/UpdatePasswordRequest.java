package com.tracku.chris.tracku.Utils.CustomRequests.Users;
import com.tracku.chris.tracku.Utils.CustomValidators.User.ComparePasswords;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComparePasswords(password = "newPassword", confirmPassword = "confirmPassword")
public class UpdatePasswordRequest {
    @NotBlank(message="Password can't be empty")
    @Pattern(regexp = "^(?=\\S)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#%^&*])[a-zA-Z0-9!@#%^&*]{8,20}$", message = "Password should be 8-20chars, contain numbers, lowercase and uppercase letters and '!@#%^&*' special chars")
    private String currentPassword;
    @NotBlank(message="New password can't be empty")
    @Pattern(regexp = "^(?=\\S)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#%^&*])[a-zA-Z0-9!@#%^&*]{8,20}$", message = "Password should be 8-20chars, contain numbers, lowercase and uppercase letters and '!@#%^&*' special chars")
    private String newPassword;
    @NotBlank(message="Confirm password can't be empty")
    private String confirmPassword;
}
