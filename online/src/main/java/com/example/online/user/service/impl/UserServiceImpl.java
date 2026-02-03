package com.example.online.user.service.impl;

import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.course.controller.CourseController;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.PostDocument;
import com.example.online.domain.model.UserDocument;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.DocumentOf;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.key.RedisKey;
import com.example.online.repository.UserRepository;
import com.example.online.user.dto.UserCredentialsUpdateRequest;
import com.example.online.user.dto.UserViewUpdateRequest;
import com.example.online.user.service.UserService;
import com.example.online.utils.BanUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BanUtils banUtils;
    private final PasswordEncoder passwordEncoder;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtService jwtService;
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void banUserFromEverything(Long userId, User user) {
        banUtils.addBanRecord(userId, BanType.EVERYTHING);
        LOG.info("User {} banned user {} (Everything)", user.getFirstName() + " " + user.getLastName(), userId);
    }

    public void unbanUser(Long userId, User user) {
        banUtils.removeAllBanRecord(userId);
        LOG.info("User {} unbanned user {} (Everything)", user.getFirstName() + " " + user.getLastName(), userId);
    }

    @Transactional
    public void updateViewUser(UserViewUpdateRequest userUpdateRequest, User authuser) {
        User user = userRepository.findById(authuser.getId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (userUpdateRequest.getFirstName() != null) {
            user.setFirstName(userUpdateRequest.getFirstName());
        }
        if (userUpdateRequest.getLastName() != null) {
            user.setLastName(userUpdateRequest.getLastName());
        }
        if (userUpdateRequest.getAddDocs() != null && !userUpdateRequest.getAddDocs().isEmpty()) {
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.USER);
            List<?> results = documentService.resolveDocument(userUpdateRequest.getAddDocs(), user);
            @SuppressWarnings("unchecked")
            List<UserDocument> postDocs = (List<UserDocument>) results;
            for (var doc : postDocs) {
                user.getDocumentURL().add(doc);
            }
        }
        if (userUpdateRequest.getRemoveDocs() != null && !userUpdateRequest.getRemoveDocs().isEmpty()) {
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.USER);
            List<?> results = documentService.resolveDocument(userUpdateRequest.getRemoveDocs(), user);
            @SuppressWarnings("unchecked")
            List<UserDocument> postDocs = (List<UserDocument>) results;
            for (var doc : postDocs) {
                user.getDocumentURL().remove(doc);
            }
        }
        userRepository.save(user);
    }

    @Transactional
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
        userRepository.save(user);

        //Use epoch timestamp to synchronize time period in the world and easily stored, compared in redis
        Long validAfter = System.currentTimeMillis() / 1000;

        String key = RedisKey.userValidAfter(user.getId());
        stringRedisTemplate.opsForValue().set(key, String.valueOf(validAfter));
    }

    @Transactional
    public void updateEmailUser(String email, User user) {

    }

    public void loggingOut(User user, String token){
        Date expire = jwtService.extractExpiration(token);
        String jti = jwtService.extractJti(token);
        String key = RedisKey.jwtBlacklist(jti);

        long ttl_Ms = expire.getTime() - System.currentTimeMillis();

        if (ttl_Ms < 0){
            ttl_Ms = 0;
        }
        stringRedisTemplate.opsForValue().set(key, "1", ttl_Ms, TimeUnit.MILLISECONDS);
    }
}
