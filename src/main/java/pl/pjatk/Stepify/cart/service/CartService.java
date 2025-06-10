package pl.pjatk.Stepify.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.pjatk.Stepify.cart.dto.CartDTO;
import pl.pjatk.Stepify.cart.dto.CartItemDTO;
import pl.pjatk.Stepify.cart.dto.RequestColorAndSizeDTO;
import pl.pjatk.Stepify.cart.exception.OutOfStockException;
import pl.pjatk.Stepify.cart.mapper.CartItemMapper;
import pl.pjatk.Stepify.cart.mapper.CartMapper;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.model.CartItem;
import pl.pjatk.Stepify.cart.repository.CartItemRepository;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.product.model.Product;
import pl.pjatk.Stepify.product.model.ProductColor;
import pl.pjatk.Stepify.product.model.ProductSize;
import pl.pjatk.Stepify.product.repository.ProductRepository;
import pl.pjatk.Stepify.product.service.ProductService;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.service.UserService;


@Service
@RequiredArgsConstructor
public class CartService {


    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductService productService;


    public CartDTO getCart() {
        User currentUser = userService.getCurrentUser();
        return cartMapper.mapCartToCartDTO(
                cartRepository.findCartByUserId(currentUser.getId())
                        .orElseGet(() -> cartRepository.save(new Cart(currentUser)))
        );
    }

    @Transactional
    public CartDTO clearCart() {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartRepository.findCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getCartItems().clear();
        cart.recalculateTotalPrice();

        return cartMapper.mapCartToCartDTO(cartRepository.save(cart)); // persist changes
    }

    public CartItemDTO addItem(String brandName, String modelName, RequestColorAndSizeDTO colorAndSize) {
        Product product = productRepository.findProductByBrandNameAndModelName(brandName, modelName)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + brandName + " " + modelName + " not found"));

        Cart cart = cartRepository.findCartByUserId(userService.getCurrentUser().getId())
                .orElseGet(() -> cartRepository.save(new Cart(userService.getCurrentUser())));

        CartItem newCartItem = new CartItem(product, colorAndSize.getColor(), colorAndSize.getSize(), 1, cart);

        if(!isColorValid(product, colorAndSize.getColor()) || !isSizeValid(product, colorAndSize.getColor(), colorAndSize.getSize())) {
            throw new ResourceNotFoundException("Product " + brandName + " " + modelName + " not found");
        }

        if (isItemInCart(cart, product, colorAndSize.getColor(), colorAndSize.getSize())) {
            if (isQuantityValid(product, colorAndSize.getColor(), colorAndSize.getSize(), 1)) {
                increaseQuantityForItem(cart, product, colorAndSize.getColor(), colorAndSize.getSize());
            } else {
                throw new OutOfStockException("Only " + productService.getAvailableStock(product, colorAndSize.getColor(), colorAndSize.getSize()) + " items left in stock");
            }
        } else {
            if (isQuantityValid(product, colorAndSize.getColor(), colorAndSize.getSize(), 1)) {
                cart.getCartItems().add(newCartItem);
            } else {
                throw new OutOfStockException("Only " + productService.getAvailableStock(product, colorAndSize.getColor(), colorAndSize.getSize()) + " items left in stock");
            }
        }

        cart.recalculateTotalPrice();
        cartRepository.save(cart);

        return cartItemMapper.mapCartItemToCartItemDTO(newCartItem);
    }

    @Transactional
    public CartDTO deleteItem(long id) {
        CartItem item = getCartItemForCurrentUser(id);
        Cart cart = item.getCart();

        cart.getCartItems().remove(item);
        cart.recalculateTotalPrice();

        return cartMapper.mapCartToCartDTO(cartRepository.save(cart));
    }

    @Transactional
    public CartDTO updateItemQuantity(long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = getCartItemForCurrentUser(id);
        Cart cart = item.getCart();

        if (isQuantityValid(item.getProduct(), item.getColor(), item.getSize(), quantity)) {
            item.setQuantity(quantity);
            cart.recalculateTotalPrice();
        } else {
            throw new OutOfStockException("Only " + productService.getAvailableStock(item.getProduct(), item.getColor(), item.getSize()) + " items left in stock");
        }

        return cartMapper.mapCartToCartDTO(cartRepository.save(cart));
    }

    private boolean isItemInCart(Cart cart, Product product, String color, double size) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return false;
        } else {
            return cart.getCartItems().stream()
                    .anyMatch(item ->
                            item.getProduct().getId() == product.getId() &&
                                    item.getColor().equalsIgnoreCase(color) &&
                                    item.getSize() == size);
        }
    }

    private void increaseQuantityForItem(Cart cart, Product product, String color, double size) {
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId() == product.getId() && item.getColor().equals(color) && item.getSize() == size) {
                int newQuantity = item.getQuantity() + 1;
                if (isQuantityValid(product, color, size, newQuantity)) {
                    item.setQuantity(item.getQuantity() + 1);
                } else {
                    throw new OutOfStockException("Only " + productService.getAvailableStock(product, color, size) + " items left in stock");
                }
            }
        }
    }

    private CartItem getCartItemForCurrentUser(long itemId) {
        User currentUser = userService.getCurrentUser();

        Cart cart = cartRepository.findCartByUserId(currentUser.getId())
                .orElseThrow();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (item.getCart().getId() != cart.getId()) {
            throw new UnauthorizedAccessException("You do not have permission to access this cart item.");
        }

        return item;
    }

    private boolean isColorValid(Product product, String color) {
        return product.getColors()
                .stream()
                .anyMatch(c -> c.getColor().equalsIgnoreCase(color));
    }

    private boolean isSizeValid(Product product, String color, double size) {
        return product.getColors()
                .stream()
                .filter(c -> c.getColor().equalsIgnoreCase(color))
                .anyMatch(c -> c.getSizes().stream()
                        .anyMatch(s -> s.getSize() == size));
    }

    private boolean isQuantityValid(Product product, String color, double size, int quantity) {
        return product.getColors()
                .stream()
                .filter(c -> c.getColor().equalsIgnoreCase(color))
                .flatMap(c -> c.getSizes().stream())
                .filter(s -> s.getSize() == size)
                .findFirst()
                .map(s -> quantity <= s.getStock())
                .orElse(false); // return false if no matching size is found
    }

}
