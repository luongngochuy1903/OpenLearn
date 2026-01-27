package com.example.online.user.controller;

import com.example.online.annotation.CurrentUser;
import com.example.online.domain.model.User;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.service.PostQueryService;
import com.example.online.post.service.PostService;
import com.example.online.user.dto.UserCredentialsUpdateRequest;
import com.example.online.user.dto.UserViewUpdateRequest;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final PostQueryService postQueryService;
    private final UserService userService;
    @GetMapping("/posts")
    public ResponseEntity<List<PostGetResponse>> getMyPostDetail(@CurrentUser User user){
        return ResponseEntity.ok(postQueryService.viewMyPostDetail(user));
    }

    @PostMapping("/banned/target/{targetManId}")
    public ResponseEntity<String> banUser(@PathVariable Long targetManId, @CurrentUser User user){
        userService.banUserFromEverything(targetManId, user);
        return ResponseEntity.ok("Banned this successfully");
    }

    @DeleteMapping("/unbanned/target/{targetManId}")
    public ResponseEntity<String> unbanUser(@PathVariable Long targetManId, @CurrentUser User user){
        userService.unbanUser(targetManId, user);
        return ResponseEntity.ok("Unbanned this successfully");
    }

    // ================= PERSONAL INFORMATION ======================
    // User view
    @PostMapping("/information/view")
    public ResponseEntity<String> updateViewUserInformation(@RequestBody UserViewUpdateRequest req,
                                                            @CurrentUser User user){
        userService.updateViewUser(req, user);
        return ResponseEntity.ok("Update personal information successfully");
    }

    // User credentials
    @PostMapping("/information/credentials")
    public ResponseEntity<String> updateCredentialsUserInformation(@RequestBody UserCredentialsUpdateRequest req,
                                                            @CurrentUser User user){
        userService.updateCredentialsUser(req, user);
        return ResponseEntity.ok("Update password successfully");
    }
}

