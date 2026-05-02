package com.exploreMate.content_service.controller;

import com.exploreMate.content_service.dto.MediaItemDto;
import com.exploreMate.content_service.dto.PageContentDto;
import com.exploreMate.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/pages")
    public ResponseEntity<List<PageContentDto>> getAllPublicPages() {
        return ResponseEntity.ok(contentService.getAllPages().stream()
                .filter(p -> "Published".equalsIgnoreCase(p.getStatus()))
                .collect(java.util.stream.Collectors.toList()));
    }

    // Admin endpoints
    @GetMapping("/admin/pages")
    public ResponseEntity<List<PageContentDto>> getAllPages(@RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.getAllPages());
    }

    @PostMapping("/admin/pages")
    public ResponseEntity<PageContentDto> createPage(
            @RequestBody PageContentDto dto,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.createPage(dto));
    }

    @PutMapping("/admin/pages/{slug}")
    public ResponseEntity<PageContentDto> updatePageContent(
            @PathVariable String slug, 
            @RequestBody PageContentDto dto,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.updatePage(slug, dto));
    }

    @DeleteMapping("/admin/pages/{slug}")
    public ResponseEntity<Void> deletePage(
            @PathVariable String slug,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        contentService.deletePage(slug);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getContentStats(
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.getStats());
    }

    // ─── Media Endpoints ────────────────────────────────────────────────────

    @GetMapping("/admin/media")
    public ResponseEntity<List<MediaItemDto>> getAllMedia(@RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.getAllMedia());
    }

    @PostMapping("/admin/media")
    public ResponseEntity<MediaItemDto> addMedia(
            @RequestBody MediaItemDto dto,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.addMedia(dto));
    }

    @PutMapping("/admin/media/{id}")
    public ResponseEntity<MediaItemDto> updateMedia(
            @PathVariable String id,
            @RequestBody MediaItemDto dto,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(contentService.updateMedia(id, dto));
    }

    @DeleteMapping("/admin/media/{id}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        contentService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/all")
    public ResponseEntity<Void> deleteAllContent(
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        if (roles == null || !roles.contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        contentService.deleteAllContent();
        return ResponseEntity.noContent().build();
    }
}
