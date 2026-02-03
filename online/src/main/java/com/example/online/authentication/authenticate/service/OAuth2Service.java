package com.example.online.authentication.authenticate.service;

import com.example.online.domain.model.User;

public interface OAuth2Service {
    String generateAuthorizationCode(User user);
}
