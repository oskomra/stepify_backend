package pl.pjatk.Stepify.cart.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
