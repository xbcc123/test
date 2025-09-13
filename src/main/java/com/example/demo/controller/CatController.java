package com.example.demo.controller;

import com.example.demo.model.Cat;
import com.example.demo.repository.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cats")
public class CatController {
    @Autowired
    private CatRepository catRepository;

    /**
     * 获取所有猫的信息
     * @return 猫列表
     */
    @GetMapping
    public List<Cat> getAllCats() {
        return catRepository.findAll();
    }

    /**
     * 新增一只猫
     * @param cat 新猫对象
     * @return 新增后的猫对象（带ID）
     */
    @PostMapping
    public Cat addCat(@RequestBody Cat cat) {
        return catRepository.save(cat);
    }

    /**
     * 根据ID获取猫的信息
     * @param id 猫ID
     * @return 对应的猫对象，若不存在返回null
     */
    @GetMapping("/{id}")
    public Cat getCatById(@PathVariable Long id) {
        Optional<Cat> cat = catRepository.findById(id);
        return cat.orElse(null);
    }

    /**
     * 根据ID更新猫的信息
     * @param id 猫ID
     * @param updatedCat 新的猫信息
     * @return 更新后的猫对象，若不存在返回null
     */
    @PutMapping("/{id}")
    public Cat updateCat(@PathVariable Long id, @RequestBody Cat updatedCat) {
        Optional<Cat> optionalCat = catRepository.findById(id);
        if (optionalCat.isPresent()) {
            Cat cat = optionalCat.get();
            cat.setName(updatedCat.getName());
            cat.setAge(updatedCat.getAge());
            return catRepository.save(cat);
        }
        return null;
    }

    /**
     * 根据ID删除猫
     * @param id 猫ID
     * @return 删除成功返回true，否则返回false
     */
    @DeleteMapping("/{id}")
    public boolean deleteCat(@PathVariable Long id) {
        if (catRepository.existsById(id)) {
            catRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
