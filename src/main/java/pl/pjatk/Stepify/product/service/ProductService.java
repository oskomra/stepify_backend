package pl.pjatk.Stepify.product.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pjatk.Stepify.exception.ResourceAlreadyExistsException;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.product.dto.product.ProductCreateDTO;
import pl.pjatk.Stepify.product.dto.product.ProductDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorCreateDTO;
import pl.pjatk.Stepify.product.dto.productColor.ProductColorUpdateDTO;
import pl.pjatk.Stepify.product.dto.productSize.ProductSizeUpdateDTO;
import pl.pjatk.Stepify.product.dto.product.ProductUpdateDTO;
import pl.pjatk.Stepify.product.mappers.ProductColorMapper;
import pl.pjatk.Stepify.product.mappers.ProductMapper;
import pl.pjatk.Stepify.product.mappers.ProductSizeMapper;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.model.ProductColor;
import pl.pjatk.Stepify.product.model.ProductFilter;
import pl.pjatk.Stepify.product.model.ProductSize;
import pl.pjatk.Stepify.product.repository.ProductRepository;
import pl.pjatk.Stepify.product.repository.ProductSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductColorMapper productColorMapper;
    private final ProductSizeMapper productSizeMapper;
    private final ProductSizeService productSizeService;
    private final ProductColorService productColorService;

    public List<ProductDTO> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductByBrandAndModel(String brandName, String modelName) {
        return productMapper.
                toDto(
                        productRepository.
                        findProductByBrandNameAndModelName(brandName, modelName)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    public List<ProductDTO> getFilteredProducts(ProductFilter filter) {
        List<Product> products = productRepository.findAll(ProductSpecification.filterByCriteria(filter));
        return products.stream()
                .map(product -> productMapper.toFilteredDto(product, filter))
                .filter(dto -> !dto.getColors().isEmpty())
                .toList();
    }

    public ProductCreateDTO addProduct(ProductCreateDTO productCreateDTO, MultipartFile imageFile) throws IOException {
        if (productRepository.findProductByBrandNameAndModelName(
                productCreateDTO.getBrandName(), productCreateDTO.getModelName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Product already exists");
        }

        String modelName = productCreateDTO.getModelName().replaceAll("\\s+", "_");
        String colorName = productCreateDTO.getColors().get(0).getColor().replaceAll("\\s+", "_");

        String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
        String newFileName = modelName + "_" + colorName + "." + extension;

        // Save to external directory: uploads/images/
        String uploadDir = "uploads/images/";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        Path imagePath = uploadPath.resolve(newFileName);
        Files.write(imagePath, imageFile.getBytes());

        // Set image path as served URL
        String imageUrl = "/images/" + newFileName;

        ProductColorCreateDTO productColorCreateDTO = productCreateDTO.getColors().get(0);
        productColorCreateDTO.setImages(List.of(imageUrl));
        productCreateDTO.setColors(List.of(productColorCreateDTO));

        Product newProduct = productMapper.toEntity(productCreateDTO);
        productRepository.save(newProduct);

        return productCreateDTO;
    }

    public void deleteProductById(long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
    }

    public ProductDTO patchProductById(long id, ProductUpdateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        productMapper.patch(product, dto);

        if (dto.getColors() != null) {
            for (ProductColorUpdateDTO colorDto : dto.getColors()) {
                ProductColor color = productColorService.findColorById(product, colorDto.getId());
                if (color != null) {
                    productColorMapper.patch(color, colorDto);

                    if (colorDto.getSizes() != null) {
                        for (ProductSizeUpdateDTO sizeDto : colorDto.getSizes()) {
                            ProductSize size = productSizeService.findSizeById(color, sizeDto.getId());
                            if (size != null) {
                                productSizeMapper.patch(size, sizeDto);
                            }
                        }
                    }
                }
            }
        }
        productRepository.save(product);
        return productMapper.toDto(product);
    }


}
