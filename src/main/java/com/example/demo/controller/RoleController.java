import com.example.demo.service.RoleService;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // ...existing code...

    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> assignPermissions(@PathVariable Long roleId, @RequestBody Set<Long> permissionIds) {
        boolean success = roleService.assignPermissions(roleId, permissionIds);
        return Map.of("success", success);
    }

    // ...existing code...
}
