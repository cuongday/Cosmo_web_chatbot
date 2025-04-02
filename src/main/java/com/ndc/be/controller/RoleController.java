package com.ndc.be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ndc.be.domain.Role;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.service.RoleService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.error.IdInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/roles")
    @ApiMessage("Create role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) throws IdInvalidException {
        if(this.roleService.existsByName(role.getName())){
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws IdInvalidException {
        Role updateRoll = this.roleService.fetchRoleById(role.getId());
        if(updateRoll == null){
            throw new IdInvalidException("Role với id = " + role.getId() + " không tồn tại");
        }
//        if(!this.roleService.existsByName(role.getName())){
//            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại");
//        }
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.updateRole(role));
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<ResultPaginationDTO> fetchRole(@Filter Specification<Role> spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAll(spec, pageable));
    }


    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        if(role == null){
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable Long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        if(role == null){
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(role);
    }

}