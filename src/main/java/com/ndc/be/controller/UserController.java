package com.ndc.be.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ndc.be.domain.User;
import com.ndc.be.domain.request.CreateUserDTO;
import com.ndc.be.domain.request.UpdateUserDTO;
import com.ndc.be.domain.response.ResUpdateUserDTO;
import com.ndc.be.domain.response.ResUserDTO;
import com.ndc.be.domain.response.ResultPaginationDTO;
import com.ndc.be.domain.response.ResCreateUserDTO;
import com.ndc.be.service.UserService;
import com.ndc.be.util.annotation.ApiMessage;
import com.ndc.be.util.error.IdInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @PreAuthorize("hasRole('admin')")
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(
            @Valid @ModelAttribute CreateUserDTO formRequest,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
        ) throws IdInvalidException {
        User user = this.userService.toEntity(formRequest);

        boolean isUsernameExist = this.userService.existsByUsername(user.getUsername());
        if(isUsernameExist) {
            throw new IdInvalidException(
                    "Username " + user.getUsername() + " đã tồn tại, vui lòng sử dụng username khác");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user, avatarFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if(fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("")
    @ApiMessage("Fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> userSpec,
            Pageable pageable
            ) {
        ResultPaginationDTO rs = this.userService.handleGetUser(userSpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id)
        throws IdInvalidException{
        User currentUser = this.userService.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    @ApiMessage("Update user by id")
    public ResponseEntity<ResUpdateUserDTO> updateUser(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute UpdateUserDTO formRequest,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
        ) throws IdInvalidException {
        User userUpdate = this.userService.handleUpdateUser(id, formRequest, avatarFile);
        if(userUpdate == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));
    }
}
