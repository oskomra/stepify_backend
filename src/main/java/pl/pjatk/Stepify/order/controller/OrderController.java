package pl.pjatk.Stepify.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.dto.OrderStatusUpdateDTO;
import pl.pjatk.Stepify.order.dto.OrderCancellationDTO;
import pl.pjatk.Stepify.order.service.OrderService;

import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public ResponseEntity<OrderDTO> order() {
        return ResponseEntity.ok(orderService.order());
    }

    @PostMapping("/order")
    public ResponseEntity<OrderSummaryDTO> placeOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.placeOrder(orderDTO));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/allOrders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/order/status/{id}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable long id,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusUpdateDTO));
    }

    @PostMapping("/order/cancel/{id}")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
