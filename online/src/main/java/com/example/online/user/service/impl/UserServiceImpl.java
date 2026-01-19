package com.example.online.user.service.impl;

import com.example.online.course.controller.CourseController;
import com.example.online.enumerate.BanType;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.repository.UserRepository;
import com.example.online.user.service.UserService;
import com.example.online.utils.BanUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BanUtils banUtils;
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    public User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void banUserFromEverything(Long userId, User user){
        banUtils.addBanRecord(userId, BanType.EVERYTHING);
        LOG.info("User {} banned user {} (Everything)", user.getFirstName() + " " + user.getLastName(), userId);
    }

    public void unbanUser(Long userId, User user){
        banUtils.removeAllBanRecord(userId);
        LOG.info("User {} unbanned user {} (Everything)", user.getFirstName() + " " + user.getLastName(), userId);
    }
}
