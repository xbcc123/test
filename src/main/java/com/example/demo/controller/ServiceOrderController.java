package com.example.demo.controller;

import com.example.demo.model.ServiceOrder;
import com.example.demo.service.ServiceOrderService;
import com.example.demo.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {
    @Autowired
    private ServiceOrderService serviceOrderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody ServiceOrder order, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("请先登录");
        ServiceOrder saved = serviceOrderService.createOrder(userDetails.getUsername(), order);
        return saved != null ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().body("预约失败");
    }

    @GetMapping("")
    public ResponseEntity<List<ServiceOrder>> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        List<ServiceOrder> list = serviceOrderService.getUserOrders(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrder> getOrder(@PathVariable Long id) {
        return serviceOrderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).body("请先登录");
        boolean ok = serviceOrderService.cancelOrder(id, userDetails.getUsername());
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("取消失败");
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        boolean ok = serviceOrderService.completeOrder(id);
        return ok ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body("操作失败");
    }
}

