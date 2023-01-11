package com.tracku.chris.tracku.Utils.CustomValidators.User;
import com.tracku.chris.tracku.Utils.CustomRequests.Users.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConfirmPasswordValidator implements ConstraintValidator<ComparePasswords, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext constraintValidatorContext){
        String passwordValue = request.getPassword();
        String confirmPasswordValue = request.getConfirmPassword();
        if(passwordValue != null) {
            return passwordValue.equals(confirmPasswordValue);
        }
        return false;
    }
}
