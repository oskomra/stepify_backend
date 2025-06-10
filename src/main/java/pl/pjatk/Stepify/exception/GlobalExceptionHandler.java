package pl.pjatk.Stepify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleNotFound(ResourceNotFoundException e) {
        return new ResponseEntity<>(new CustomErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorMessage> handleInputArgumentNotValid(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getDefaultMessage()).append("\n")
        );
        return new ResponseEntity<>(new CustomErrorMessage(errorMessage.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<CustomErrorMessage> handleUnauthorizedAccess(UnauthorizedAccessException e) {
        CustomErrorMessage errorMessage = new CustomErrorMessage(e.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<CustomErrorMessage> handleAlreadyExists(ResourceAlreadyExistsException e) {
        return new ResponseEntity<>(new CustomErrorMessage(e.getMessage()), HttpStatus.CONFLICT);
    }

}
