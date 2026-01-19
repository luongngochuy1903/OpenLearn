package com.example.online.user.service;

import com.example.online.domain.model.User;

public interface UserService {
    User findUserById(Long id);
    void banUserFromEverything(Long userId, User user);
    void unbanUser(Long userId, User user);
}
