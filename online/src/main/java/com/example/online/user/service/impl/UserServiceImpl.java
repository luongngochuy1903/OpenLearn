package com.example.online.user.service.impl;

import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.repository.UserRepository;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
