package com.example.demo.service;

import com.example.demo.model.ServiceOrder;
import com.example.demo.model.User;
import com.example.demo.repository.ServiceOrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceOrderService {
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private UserRepository userRepository;

    public ServiceOrder createOrder(String username, ServiceOrder order) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        order.setUser(user);
        order.setStatus("待处理");
        return serviceOrderRepository.save(order);
    }

    public List<ServiceOrder> getUserOrders(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return List.of();
        return serviceOrderRepository.findByUser(user);
    }

    public Optional<ServiceOrder> getOrder(Long id) {
        return serviceOrderRepository.findById(id);
    }

    public boolean cancelOrder(Long id, String username) {
        Optional<ServiceOrder> opt = serviceOrderRepository.findById(id);
        if (opt.isPresent() && opt.get().getUser().getUsername().equals(username)) {
            ServiceOrder order = opt.get();
            order.setStatus("已取消");
            serviceOrderRepository.save(order);
            return true;
        }
        return false;
    }

    public boolean completeOrder(Long id) {
        Optional<ServiceOrder> opt = serviceOrderRepository.findById(id);
        if (opt.isPresent()) {
            ServiceOrder order = opt.get();
            order.setStatus("已完成");
            serviceOrderRepository.save(order);
            return true;
        }
        return false;
    }
}

