package com.tracku.chris.tracku.Utils.ExceptionHandlers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class FieldValidatorExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> responseObject = new HashMap<>();
        String error = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        responseObject.put("Timestamp: ", LocalDateTime.now());
        responseObject.put("StatusCode", status.value());
        responseObject.put("Status", status);
        responseObject.put("Error", error);
        return new ResponseEntity<>(responseObject, status);
    }
}
