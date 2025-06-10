package pl.pjatk.Stepify.promotion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PromotionExceptionHandler {

    @ExceptionHandler(InvalidPromotionException.class)
    public ResponseEntity<CustomErrorMessage> handleInvalidPromotionException(InvalidPromotionException e) {
        return new ResponseEntity<>(new CustomErrorMessage(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
