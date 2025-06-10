package pl.pjatk.Stepify.promotion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.cart.dto.CartDTO;
import pl.pjatk.Stepify.cart.service.CartService;
import pl.pjatk.Stepify.promotion.dto.ApplyPromotionDTO;
import pl.pjatk.Stepify.promotion.dto.PromotionDTO;
import pl.pjatk.Stepify.promotion.exception.InvalidPromotionException;
import pl.pjatk.Stepify.promotion.service.PromotionService;

import java.util.List;

@RequestMapping("/promotions")
@RestController
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final CartService cartService;


    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionService.createPromotion(promotionDTO));
    }

    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @PostMapping("/apply")
    public ResponseEntity<CartDTO> applyPromotion(@RequestBody ApplyPromotionDTO applyPromotionDTO) {
        try {
            return ResponseEntity.ok(cartService.applyPromotion(applyPromotionDTO));
        } catch (InvalidPromotionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CartDTO> removePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.removePromotion(id));
    }

}
