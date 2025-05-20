package pl.pjatk.Stepify.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.Stepify.order.dto.OrderDTO;
import pl.pjatk.Stepify.order.dto.OrderSummaryDTO;
import pl.pjatk.Stepify.order.model.Order;
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
    public ResponseEntity<OrderSummaryDTO> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }
}
