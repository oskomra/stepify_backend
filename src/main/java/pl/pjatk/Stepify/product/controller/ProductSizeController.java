package pl.pjatk.Stepify.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeCreateDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;
import pl.pjatk.Stepify.product.service.ProductSizeService;

@RestController
@RequestMapping("/sizes")
@RequiredArgsConstructor
public class ProductSizeController {

    private final ProductSizeService productSizeService;


    @GetMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> findProductSizeById(@PathVariable long id) {
        return ResponseEntity.ok(productSizeService.findProductSizeById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> patchProductSizeById(@PathVariable long id,@Valid @RequestBody ProductSizeUpdateDTO productSizeUpdateDTO) {
        return ResponseEntity.ok(productSizeService.patchProductSizeById(id, productSizeUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductSizeById(@PathVariable long id) {
        productSizeService.deleteProductSizeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProductSizeDTO> addProductSizeById(@PathVariable long id,@Valid @RequestBody ProductSizeCreateDTO productSizeCreateDTO) {
        return ResponseEntity.ok(productSizeService.addProductSizeById(id, productSizeCreateDTO));
    }

}
