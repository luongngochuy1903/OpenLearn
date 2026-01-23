package com.example.online.user.service.impl;

import com.example.online.course.controller.CourseController;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.DocumentOf;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.repository.UserRepository;
import com.example.online.user.dto.UserCredentialsUpdateRequest;
import com.example.online.user.dto.UserViewUpdateRequest;
import com.example.online.user.service.UserService;
import com.example.online.utils.BanUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BanUtils banUtils;
    private final PasswordEncoder passwordEncoder;
    private final DocumentGenerateFactory documentGenerateFactory;
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

    public void updateViewUser(UserViewUpdateRequest userUpdateRequest, User user){
        if (userUpdateRequest.getFirstName() != null){
            user.setFirstName(userUpdateRequest.getFirstName());
        }
        if (userUpdateRequest.getLastName() != null){
            user.setLastName(userUpdateRequest.getLastName());
        }
        DocumentService documentService = documentGenerateFactory.getService(DocumentOf.USER);
        for (var documentReq : userUpdateRequest.getDocs()) {
            documentService.createDocument(user, documentReq);
        }
    }

    public void updateCredentialsUser(UserCredentialsUpdateRequest request, User user) {
        // 3 fields validated
        if (request.getCurrentPassword() == null ||
                request.getPassword() == null ||
                request.getConfirmPassword() == null) {

            throw new BadRequestException("Please enter all required fields");
        }

        // true current password
        // matches(raw password, encoded password)
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // 3. Check password matching confirmPassword
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Confirm password does not match");
        }

        // 4. Update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
}
