package com.exploreMate.qr_guide_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QrArtifactResponse {
    private String id;
    private String title;
    private String location;
    private String description;
    private String image;
    private String audioDuration;
    private String year;
    private List<String> tags;
}
