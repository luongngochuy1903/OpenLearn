package com.example.online.user.service;

import com.example.online.domain.model.User;
import com.example.online.user.dto.UserCredentialsUpdateRequest;
import com.example.online.user.dto.UserViewUpdateRequest;

public interface UserService {
    User findUserById(Long id);
    void banUserFromEverything(Long userId, User user);
    void unbanUser(Long userId, User user);
    void updateViewUser(UserViewUpdateRequest userUpdateRequest, User user);
    void updateCredentialsUser(UserCredentialsUpdateRequest request, User user);
}
