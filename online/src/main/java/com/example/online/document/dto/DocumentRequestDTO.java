package com.example.online.document.dto;

import com.example.online.enumerate.UploadType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DocumentRequestDTO {
    private Long id;
    private String objectKey;
    private UploadType type;
    private Number size;
}
