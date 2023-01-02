package com.tracku.chris.tracku.CustomValidators.Handlers;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class FieldValidatorExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("Timestamp: ", new Date());
        responseObject.put("Status", status.value());
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map((error) -> error.getDefaultMessage()).toList();
        responseObject.put("Error :",errors);
        return new ResponseEntity<>(responseObject, status);
    }
}
