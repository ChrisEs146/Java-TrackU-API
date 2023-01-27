package com.tracku.chris.tracku.Utils.CustomValidators.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class ConfirmPasswordValidator implements ConstraintValidator<ComparePasswords, Object> {
    private String password;
    private String confirmPassword;

    @Override
    public void initialize(ComparePasswords constraint) {
        this.password = constraint.password();
        this.confirmPassword = constraint.confirmPassword();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext){
        Object passwordValue = new BeanWrapperImpl(value).getPropertyValue(password);
        Object confirmPasswordValue = new BeanWrapperImpl(value).getPropertyValue(confirmPassword);
        if(passwordValue != null) {
            return passwordValue.equals(confirmPasswordValue);
        }
        return false;
    }
}
