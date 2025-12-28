package com.example.online.utils;

import com.example.online.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("Cannot load this user: User unauthorized 400");
            return null;
        }

        return (User) authentication.getPrincipal();
    }
}
