package com.exploreMate.content_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaItemDto {
    private String id;
    private String name;
    private String url;
    private String type;
    private String sizeLabel;
    private String status;
    private String createdAt;
    private String updatedAt;
}
