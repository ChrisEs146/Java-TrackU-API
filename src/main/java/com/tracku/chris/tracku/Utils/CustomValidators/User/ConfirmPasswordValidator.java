package com.tracku.chris.tracku.Utils.CustomValidators.User;
import com.tracku.chris.tracku.DTOs.UserDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidator implements ConstraintValidator<ComparePasswords, UserDTO> {

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext constraintValidatorContext){
        String passwordValue = user.getPassword();
        String confirmPasswordValue = user.getConfirmPassword();
        return confirmPasswordValue.equals(passwordValue);
    }
}
