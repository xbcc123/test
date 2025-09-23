package com.example.demo.service;

import com.example.demo.model.ServiceOrder;
import com.example.demo.model.User;
import com.example.demo.model.dto.ServiceOrderDTO;
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

    public ServiceOrderDTO createOrder(String username, ServiceOrder order) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return null;
        order.setUser(user);
        order.setStatus("待处理");
        ServiceOrder saved = serviceOrderRepository.save(order);
        return toDTO(saved);
    }

    public List<ServiceOrderDTO> getUserOrdersDTO(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return List.of();
        return serviceOrderRepository.findByUser(user).stream().map(this::toDTO).toList();
    }

    public Optional<ServiceOrderDTO> getOrderDTO(Long id) {
        return serviceOrderRepository.findById(id).map(this::toDTO);
    }

    private ServiceOrderDTO toDTO(ServiceOrder o) {
        if (o == null) return null;
        ServiceOrderDTO dto = new ServiceOrderDTO();
        dto.setId(o.getId());
        if (o.getUser() != null) {
            dto.setUserId(o.getUser().getId());
            dto.setUsername(o.getUser().getUsername());
        }
        dto.setPetName(o.getPetName());
        dto.setPetType(o.getPetType());
        dto.setServiceTime(o.getServiceTime());
        dto.setAddress(o.getAddress());
        dto.setPhone(o.getPhone());
        dto.setRemark(o.getRemark());
        dto.setStatus(o.getStatus());
        dto.setCreateTime(o.getCreateTime());
        return dto;
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
