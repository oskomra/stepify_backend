package pl.pjatk.Stepify.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorCreateDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorUpdateDTO;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.service.ProductColorService;

import java.io.IOException;

@RestController
@RequestMapping("/colors")
@RequiredArgsConstructor
public class ProductColorController {

    private final ProductColorService productColorService;


    @GetMapping("/{id}")
    public ResponseEntity<ProductColorDTO> findProductColorById(@PathVariable long id) {
        return ResponseEntity.ok(productColorService.findProductColorById(id));
    }

    @PostMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductColorDTO> addProductColorById(@PathVariable long id, @RequestPart("color") @Valid ProductColorCreateDTO productColorCreateDTO,
                                                               @RequestPart("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok(productColorService.addProductColorById(id, productColorCreateDTO, imageFile));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductColorDTO> patchProductColorById(@PathVariable long id, @RequestBody ProductColorUpdateDTO productColorUpdateDTO) {
        return ResponseEntity.ok(productColorService.patchProductColorById(id, productColorUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductColorDTO> deleteProductColorById(@PathVariable long id) {
        productColorService.deleteProductColorById(id);
        return ResponseEntity.noContent().build();
    }
}
