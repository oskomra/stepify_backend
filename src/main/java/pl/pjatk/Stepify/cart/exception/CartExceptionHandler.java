package pl.pjatk.Stepify.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pjatk.Stepify.exception.CustomErrorMessage;

@RestControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<CustomErrorMessage> handleNotFound(OutOfStockException e) {
        return new ResponseEntity<>(new CustomErrorMessage(e.getMessage()), HttpStatus.CONFLICT);
    }
}
