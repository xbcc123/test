package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Order createOrder(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Double totalPrice = Double.valueOf(payload.get("totalPrice").toString());
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) payload.get("items");
        List<OrderItem> items = itemsData.stream().map(data -> {
            OrderItem item = new OrderItem();
            item.setProductId(Long.valueOf(data.get("productId").toString()));
            item.setProductName(data.get("productName").toString());
            item.setPrice(Double.valueOf(data.get("price").toString()));
            item.setQuantity(Integer.valueOf(data.get("quantity").toString()));
            item.setImgUrl(data.getOrDefault("imgUrl", "").toString());
            return item;
        }).toList();
        return orderService.createOrder(userId, items, totalPrice);
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return order == null ? null : toDTO(order);
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        List<OrderItemDTO> items = order.getItems() == null ? List.of() : order.getItems().stream().map(item -> {
            OrderItemDTO it = new OrderItemDTO();
            it.setId(item.getId());
            it.setProductId(item.getProductId());
            it.setProductName(item.getProductName());
            it.setPrice(item.getPrice());
            it.setQuantity(item.getQuantity());
            it.setImgUrl(item.getImgUrl());
            return it;
        }).collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }
}
