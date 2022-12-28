package com.tracku.chris.tracku.Models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private String userImage;
}
