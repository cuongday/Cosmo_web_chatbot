package com.ndc.be.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.ndc.be.service.UserService;

import java.util.Collections;


@Component("userDetailService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.ndc.be.domain.User user = this.userService.handleGetUserByUserName(username);

        if(user == null) {
            throw new UsernameNotFoundException("Username/ password không hợp lệ ");
        }
        String role = user.getRole().getName();
        return new User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
