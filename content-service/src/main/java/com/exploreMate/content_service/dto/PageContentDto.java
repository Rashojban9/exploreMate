package com.exploreMate.content_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageContentDto {
    private String slug;
    private String title;
    private Map<String, String> contentBlocks;
    private String status;
    private String updatedAt;
}
