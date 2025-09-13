package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String services; // 逗号分隔
    private String description;

    // getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

