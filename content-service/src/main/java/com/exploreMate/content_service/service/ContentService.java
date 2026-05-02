package com.exploreMate.content_service.service;

import com.exploreMate.content_service.dto.PageContentDto;
import com.exploreMate.content_service.dto.MediaItemDto;
import com.exploreMate.content_service.model.PageContent;
import com.exploreMate.content_service.model.MediaItem;
import com.exploreMate.content_service.repository.PageContentRepository;
import com.exploreMate.content_service.repository.MediaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final PageContentRepository repository;
    private final MediaItemRepository mediaRepository;

    public PageContentDto getPageBySlug(String slug) {
        PageContent page = repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Page not found"));
        return mapToDto(page);
    }

    public List<PageContentDto> getAllPages() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PageContentDto createPage(PageContentDto dto) {
        // Check if slug already exists
        if (repository.findBySlug(dto.getSlug()).isPresent()) {
            throw new RuntimeException("Page with slug '" + dto.getSlug() + "' already exists");
        }

        PageContent page = new PageContent();
        page.setSlug(dto.getSlug());
        page.setTitle(dto.getTitle());
        page.setContentBlocks(dto.getContentBlocks() != null ? dto.getContentBlocks() : new HashMap<>());
        page.setStatus(dto.getStatus() != null ? dto.getStatus() : "Draft");
        page.setUpdatedAt(Instant.now());

        PageContent saved = repository.save(page);
        return mapToDto(saved);
    }

    public PageContentDto updatePage(String slug, PageContentDto dto) {
        PageContent page = repository.findBySlug(slug).orElse(new PageContent());
        
        page.setSlug(slug);
        page.setTitle(dto.getTitle() != null ? dto.getTitle() : page.getTitle());
        page.setContentBlocks(dto.getContentBlocks() != null ? dto.getContentBlocks() : page.getContentBlocks());
        page.setStatus(dto.getStatus() != null ? dto.getStatus() : (page.getStatus() == null ? "Published" : page.getStatus()));
        page.setUpdatedAt(Instant.now());
        
        PageContent saved = repository.save(page);
        return mapToDto(saved);
    }

    public void deletePage(String slug) {
        PageContent page = repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Page not found"));
        repository.delete(page);
    }

    public Map<String, Object> getStats() {
        List<PageContent> allPages = repository.findAll();
        long totalPages = allPages.size();
        long publishedCount = allPages.stream()
                .filter(p -> "Published".equalsIgnoreCase(p.getStatus()))
                .count();
        long draftCount = totalPages - publishedCount;

        List<MediaItem> allMedia = mediaRepository.findAll();
        long totalMedia = allMedia.size();
        long liveMediaCount = allMedia.stream()
                .filter(m -> "Live".equalsIgnoreCase(m.getStatus()))
                .count();
        long archivedMediaCount = totalMedia - liveMediaCount;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPages", totalPages);
        stats.put("publishedCount", publishedCount);
        stats.put("draftCount", draftCount);
        stats.put("totalMedia", totalMedia);
        stats.put("liveMediaCount", liveMediaCount);
        stats.put("archivedMediaCount", archivedMediaCount);
        return stats;
    }

    // ─── Media Management ───────────────────────────────────────────────────

    public List<MediaItemDto> getAllMedia() {
        return mediaRepository.findAll().stream()
                .map(this::mapToMediaDto)
                .collect(Collectors.toList());
    }

    public MediaItemDto addMedia(MediaItemDto dto) {
        MediaItem media = MediaItem.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .type(dto.getType())
                .sizeLabel(dto.getSizeLabel())
                .status(dto.getStatus() != null ? dto.getStatus() : "Live")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapToMediaDto(mediaRepository.save(media));
    }

    public MediaItemDto updateMedia(String id, MediaItemDto dto) {
        MediaItem media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        
        if (dto.getName() != null) media.setName(dto.getName());
        if (dto.getUrl() != null) media.setUrl(dto.getUrl());
        if (dto.getType() != null) media.setType(dto.getType());
        if (dto.getSizeLabel() != null) media.setSizeLabel(dto.getSizeLabel());
        if (dto.getStatus() != null) media.setStatus(dto.getStatus());
        media.setUpdatedAt(Instant.now());
        
        return mapToMediaDto(mediaRepository.save(media));
    }

    public void deleteMedia(String id) {
        mediaRepository.deleteById(id);
    }

    public void deleteAllContent() {
        repository.deleteAll();
        mediaRepository.deleteAll();
    }

    private PageContentDto mapToDto(PageContent page) {
        return PageContentDto.builder()
                .slug(page.getSlug())
                .title(page.getTitle())
                .contentBlocks(page.getContentBlocks())
                .status(page.getStatus())
                .updatedAt(page.getUpdatedAt() != null ? page.getUpdatedAt().toString() : null)
                .build();
    }

    private MediaItemDto mapToMediaDto(MediaItem item) {
        return MediaItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .url(item.getUrl())
                .type(item.getType())
                .sizeLabel(item.getSizeLabel())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().toString() : null)
                .updatedAt(item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null)
                .build();
    }
}
