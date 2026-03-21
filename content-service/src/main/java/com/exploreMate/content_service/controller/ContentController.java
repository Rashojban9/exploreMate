package com.exploreMate.content_service.controller;

import com.exploreMate.content_service.dto.PageContentDto;
import com.exploreMate.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // Public endpoint to fetch page content
    @GetMapping("/pages/{slug}")
    public ResponseEntity<PageContentDto> getPageContent(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(contentService.getPageBySlug(slug));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Admin endpoints
    @GetMapping("/admin/pages")
    public ResponseEntity<List<PageContentDto>> getAllPages(@RequestHeader("X-User-Roles") String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.getAllPages());
    }

    @PutMapping("/admin/pages/{slug}")
    public ResponseEntity<PageContentDto> updatePageContent(
            @PathVariable String slug, 
            @RequestBody PageContentDto dto,
            @RequestHeader("X-User-Roles") String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.updatePage(slug, dto));
    }
}
