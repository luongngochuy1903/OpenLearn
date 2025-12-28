package com.example.online.user.service;

import com.example.online.domain.model.User;

public interface UserService {
    User findUserById(Long id);
}
