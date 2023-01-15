package com.tracku.chris.tracku.Utils.CustomRequests.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRequest {
    @Email(message="Provide a valid email")
    @NotBlank(message = "Email can't be empty")
    private String email;

    @NotBlank(message="Password can't be empty")
    @Pattern(regexp = "^(?=\\S)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#%^&*])[a-zA-Z0-9!@#%^&*]{8,20}$", message = "Password should be 8-20chars, contain numbers, lowercase and uppercase letters and '!@#%^&*' special chars")
    private String password;
}
