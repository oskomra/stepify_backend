package pl.pjatk.Stepify.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.pjatk.Stepify.product.dto.product.ProductCreateDTO;
import pl.pjatk.Stepify.product.dto.product.ProductDTO;
import pl.pjatk.Stepify.product.dto.product.ProductUpdateDTO;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.model.ProductFilter;
import pl.pjatk.Stepify.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/{brandName}/{modelName}")
    public ResponseEntity<ProductDTO> getProductByBrandAndModel(@PathVariable("brandName") String brandName,
                                                                @PathVariable("modelName") String modelName) {
        return ResponseEntity.ok(productService.getProductByBrandAndModel(brandName, modelName));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ProductDTO>> getFilteredProducts(@Valid @RequestBody ProductFilter filter) {
        return ResponseEntity.ok(productService.getFilteredProducts(filter));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductCreateDTO> addProduct(@RequestPart("product") @Valid ProductCreateDTO productCreateDTO,
                                                       @RequestPart("image") MultipartFile imageFile) throws IOException {
        return ResponseEntity.ok(productService.addProduct(productCreateDTO, imageFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> patchProductById(@PathVariable Long id, @RequestBody ProductUpdateDTO productUpdateDTO) {
        return ResponseEntity.ok(productService.patchProductById(id, productUpdateDTO));
    }

    @GetMapping("/products/stock/{id}")
    public ResponseEntity<Integer> getProductStockById(
            @PathVariable Long id,
            @RequestParam String color,
            @RequestParam double size) {
        return ResponseEntity.ok(productService.getProductStockById(id, color, size));
    }



}
