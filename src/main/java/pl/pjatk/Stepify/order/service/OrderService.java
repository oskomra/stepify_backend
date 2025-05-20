package pl.pjatk.Stepify.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.cart.model.Cart;
import pl.pjatk.Stepify.cart.repository.CartRepository;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.mapper.OrderMapper;
import pl.pjatk.Stepify.order.model.*;
import pl.pjatk.Stepify.order.repository.OrderRepository;
import pl.pjatk.Stepify.user.mapper.AddressMapper;
import pl.pjatk.Stepify.user.model.Address;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.repository.AddressRepository;
import pl.pjatk.Stepify.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    public OrderDTO order() {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findCartByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<OrderItem> orderItems = cart
                .getCartItems()
                .stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBrandName(cartItem.getProduct().getBrandName());
                    orderItem.setModelName(cartItem.getProduct().getModelName());
                    orderItem.setColor(cartItem.getColor());
                    orderItem.setSize(cartItem.getSize());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    return orderItem;
                }).toList();

        Order order = new Order(
                orderItems,
                cart.getTotalPrice()
        );
        orderItems.forEach(orderItem -> {orderItem.setOrder(order);});

        OrderDTO orderDTO = orderMapper.mapOrderToOrderDTO(order);
        List<Address> userAddresses = addressRepository.findByUserId(user.getId());
        orderDTO.setAvailableAddresses(addressMapper.mapListAddressToAddressDTO(userAddresses));


        return orderDTO;
    }

    public OrderSummaryDTO placeOrder(Order order) {

        User user = userService.getCurrentUser();

        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);

        return orderMapper.mapOrderToOrderSummaryDTO(orderRepository.save(order));
    }

    public List<OrderDTO> getOrders() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findAllByUserId(user.getId());

        return orders
                .stream()
                .map(orderMapper::mapOrderToOrderDTO)
                .collect(Collectors.toList());
    }
}
