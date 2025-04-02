package com.ndc.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.ndc.be.domain.Role;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.repository.RoleRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        return this.roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role updateRole(Role role){
        Role roleDB = this.fetchRoleById(role.getId());
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

    public Role fetchRoleById(long id){
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageRole.getContent());

        return rs;
    }

    public void deleteById(long id){
        this.roleRepository.deleteById(id);
    }
}
