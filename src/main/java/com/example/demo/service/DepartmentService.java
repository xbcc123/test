package com.example.demo.service;

import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.model.dto.DepartmentSimpleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    // 返回整棵树（根节点列表）
    public List<DepartmentSimpleDTO> listAll() {
        List<Department> entities = departmentRepository.findAll();
        if (entities.isEmpty()) {
            log.warn("Department JPA findAll() returned empty. Attempting fallback query on 'departments' table.");
            List<DepartmentSimpleDTO> alt = fetchFromPluralTable();
            if (!alt.isEmpty()) {
                log.info("Loaded {} departments from fallback plural table.", alt.size());
                return buildTreeFromDTOs(alt);
            }
        }
        return buildTree(entities);
    }

    // 根节点及其子树（与 listAll 结果相同，但语义保留）
    public List<DepartmentSimpleDTO> listRoot() {
        return listAll();
    }

    // 指定父节点的直接子列表（每个子节点包含其递归 children）
    public List<DepartmentSimpleDTO> listChildren(Long parentId) {
        if (parentId == null) return Collections.emptyList();
        List<DepartmentSimpleDTO> roots = buildTree(departmentRepository.findAll());
        DepartmentSimpleDTO parent = findNode(roots, parentId);
        return parent == null ? Collections.emptyList() : parent.getChildren();
    }

    public DepartmentSimpleDTO create(DepartmentSimpleDTO dto) {
        Department d = new Department();
        d.setName(dto.getName());
        d.setCode(dto.getCode());
        d.setDescription(dto.getDescription());
        if (dto.getParentId() != null) {
            departmentRepository.findById(dto.getParentId()).ifPresent(d::setParent);
        }
        Department saved = departmentRepository.save(d);
        // rebuild tree to include children placeholder
        List<DepartmentSimpleDTO> tree = buildTree(departmentRepository.findAll());
        return findNode(tree, saved.getId());
    }

    public DepartmentSimpleDTO update(Long id, DepartmentSimpleDTO dto) {
        Department d = departmentRepository.findById(id).orElse(null);
        if (d == null) return null;
        d.setName(dto.getName());
        d.setCode(dto.getCode());
        d.setDescription(dto.getDescription());
        if (dto.getParentId() != null) {
            departmentRepository.findById(dto.getParentId()).ifPresent(d::setParent);
        } else {
            d.setParent(null);
        }
        Department saved = departmentRepository.save(d);
        List<DepartmentSimpleDTO> tree = buildTree(departmentRepository.findAll());
        return findNode(tree, saved.getId());
    }

    public boolean delete(Long id) {
        if (!departmentRepository.existsById(id)) return false;
        departmentRepository.deleteById(id);
        return true;
    }

    private DepartmentSimpleDTO toDTO(Department d) {
        if (d == null) return null;
        return new DepartmentSimpleDTO(d.getId(), d.getName(), d.getCode(), d.getDescription(), d.getParent()==null?null:d.getParent().getId());
    }

    private List<DepartmentSimpleDTO> buildTree(List<Department> entities) {
        Map<Long, DepartmentSimpleDTO> map = new HashMap<>();
        // 初始化 DTO 并预置空 children
        for (Department d : entities) {
            DepartmentSimpleDTO dto = toDTO(d);
            dto.setChildren(new ArrayList<>());
            map.put(dto.getId(), dto);
        }
        List<DepartmentSimpleDTO> roots = new ArrayList<>();
        for (Department d : entities) {
            DepartmentSimpleDTO dto = map.get(d.getId());
            if (d.getParent() != null) {
                DepartmentSimpleDTO parentDTO = map.get(d.getParent().getId());
                if (parentDTO != null) parentDTO.getChildren().add(dto);
            } else {
                roots.add(dto);
            }
        }
        // 可按名称排序
        sortRecursively(roots);
        return roots;
    }

    private void sortRecursively(List<DepartmentSimpleDTO> list) {
        list.sort(Comparator.comparing(o -> Optional.ofNullable(o.getName()).orElse("")));
        for (DepartmentSimpleDTO d : list) {
            if (d.getChildren() != null && !d.getChildren().isEmpty()) sortRecursively(d.getChildren());
        }
    }

    private DepartmentSimpleDTO findNode(List<DepartmentSimpleDTO> nodes, Long id) {
        for (DepartmentSimpleDTO n : nodes) {
            if (Objects.equals(n.getId(), id)) return n;
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                DepartmentSimpleDTO r = findNode(n.getChildren(), id);
                if (r != null) return r;
            }
        }
        return null;
    }

    // helper to build tree from DTO list (used for fallback path)
    private List<DepartmentSimpleDTO> buildTreeFromDTOs(List<DepartmentSimpleDTO> flat) {
        Map<Long, DepartmentSimpleDTO> map = new HashMap<>();
        flat.forEach(d -> { d.setChildren(new ArrayList<>()); map.put(d.getId(), d); });
        List<DepartmentSimpleDTO> roots = new ArrayList<>();
        for (DepartmentSimpleDTO d : flat) {
            if (d.getParentId() != null && map.containsKey(d.getParentId())) {
                map.get(d.getParentId()).getChildren().add(d);
            } else {
                roots.add(d);
            }
        }
        sortRecursively(roots);
        return roots;
    }

    private List<DepartmentSimpleDTO> fetchFromPluralTable() {
        if (jdbcTemplate == null) return Collections.emptyList();
        try {
            return jdbcTemplate.query("SELECT id,name,code,description,parent_id FROM departments", (rs, i) -> {
                DepartmentSimpleDTO dto = new DepartmentSimpleDTO(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getObject("parent_id") == null ? null : rs.getLong("parent_id")
                );
                dto.setChildren(new ArrayList<>());
                return dto;
            });
        } catch (Exception e) {
            log.debug("Plural table fallback failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
