//package com.example.online.service.impl;
//
//import com.example.online.model.Comment;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CommentServiceImpl {
//
//    private final CommentRepository commentRepository;
//
//    public List<CommentTreeResponse> getCommentTree(Long postId) {
//        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
//
//        Map<String, CommentTreeResponse> map = new HashMap<>();
//        List<CommentTreeResponse> roots = new ArrayList<>();
//
//        // Convert tất cả thành DTO map
//        comments.forEach(c -> {
//            CommentTreeResponse dto = new CommentTreeResponse();
//            dto.setId(c.getId());
//            dto.setUserId(c.getUserId());
//            dto.setContent(c.getContent());
//            dto.setCreatedAt(c.getCreatedAt());
//            map.put(c.getId(), dto);
//        });
//
//        // Tạo cây
//        comments.forEach(c -> {
//            if (c.getParentId() == null) {
//                roots.add(map.get(c.getId()));
//            } else {
//                CommentTreeResponse parent = map.get(c.getParentId());
//                if (parent != null) {
//                    parent.getReplies().add(map.get(c.getId()));
//                }
//            }
//        });
//
//        return roots;
//    }
//}
//
