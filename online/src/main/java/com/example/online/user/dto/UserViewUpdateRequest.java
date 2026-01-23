package com.example.online.user.dto;

import com.example.online.document.dto.DocumentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserViewUpdateRequest {
    private String firstName;
    private String lastName;
    private List<DocumentRequestDTO> docs;
}
