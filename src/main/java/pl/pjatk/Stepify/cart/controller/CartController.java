package pl.pjatk.Stepify.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.cart.dto.CartDTO;
import pl.pjatk.Stepify.cart.dto.CartItemDTO;
import pl.pjatk.Stepify.cart.dto.RequestColorAndSizeDTO;
import pl.pjatk.Stepify.cart.service.CartService;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{brandName}/{modelName}")
    ResponseEntity<CartItemDTO> addItem(@PathVariable String brandName, @PathVariable String modelName, @RequestBody @Valid RequestColorAndSizeDTO colorAndSize) {
        return ResponseEntity.ok(cartService.addItem(brandName, modelName, colorAndSize));
    }

    @GetMapping
    ResponseEntity<CartDTO> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<CartDTO> removeItem(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cartService.deleteItem(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable("id") Long itemId,
                                                      @RequestParam("quantity") int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, quantity));
    }

}
