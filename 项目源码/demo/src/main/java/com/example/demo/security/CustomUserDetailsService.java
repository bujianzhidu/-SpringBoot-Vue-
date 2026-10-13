package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", username);
        
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
        
        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        logger.info("User found: {}", user.getUsername());
        return createUserDetails(user);
    }

    public UserDetails loadUserById(Long userId) {
        logger.info("Loading user by ID: {}", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            logger.warn("User not found with ID: {}", userId);
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }
        
        logger.info("User found: {}", user.getUsername());
        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        logger.info("Creating UserDetails for user: {}", user.getUsername());
        
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        logger.info("UserDetails created with authorities: {}", userDetails.getAuthorities());
        return userDetails;
    }
} 