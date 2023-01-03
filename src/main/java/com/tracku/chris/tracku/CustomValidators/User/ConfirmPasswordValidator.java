package com.tracku.chris.tracku.CustomValidators.User;
import com.tracku.chris.tracku.Models.UserModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidator implements ConstraintValidator<ComparePasswords, UserModel> {

    @Override
    public boolean isValid(UserModel user, ConstraintValidatorContext constraintValidatorContext){
        String passwordValue = user.getPassword();
        String confirmPasswordValue = user.getConfirmPassword();
        return confirmPasswordValue.equals(passwordValue);
    }
}
