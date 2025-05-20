package pl.pjatk.Stepify.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeCreateDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;
import pl.pjatk.Stepify.product.mappers.ProductSizeMapper;
import pl.pjatk.Stepify.product.model.ProductColor;
import pl.pjatk.Stepify.product.model.ProductSize;
import pl.pjatk.Stepify.product.repository.ProductColorRepository;
import pl.pjatk.Stepify.product.repository.ProductSizeRepository;

@Service
@RequiredArgsConstructor
public class ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductSizeMapper productSizeMapper;
    private final ProductColorRepository productColorRepository;

    public ProductSizeDTO findProductSizeById(long id) {
        ProductSize productSize = productSizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductSize with id " + id + " not found"));

        return productSizeMapper.toDto(productSize);
    }

    public ProductSizeDTO patchProductSizeById(long id, ProductSizeUpdateDTO productSizeUpdateDTO) {
        ProductSize productSize = productSizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductSize with id " + id + " not found"));

        productSizeMapper.patch(productSize, productSizeUpdateDTO);
        productSizeRepository.save(productSize);
        return productSizeMapper.toDto(productSize);
    }

    public void deleteProductSizeById(long id) {
        if (productSizeRepository.existsById(id)) {
            productSizeRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("ProductSize with id " + id + " not found");
        }
    }

    public ProductSize findSizeById(ProductColor color, long sizeId) {
        if (color.getSizes() != null) {
            return color.getSizes().stream()
                    .filter(s -> s.getId() == sizeId)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Size not found for this product"));
        } else {
            throw new ResourceNotFoundException("Size not found for this product");
        }
    }

    public ProductSizeDTO addProductSizeById(long id, ProductSizeCreateDTO productSizeCreateDTO) {
        ProductColor productColor = productColorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductColor with id " + id + " not found"));
        ProductSize productSize = productSizeMapper.toEntity(productSizeCreateDTO);
        productSize.setProductColor(productColor);

        productSizeRepository.save(productSize);
        return productSizeMapper.toDto(productSize);
    }


}
