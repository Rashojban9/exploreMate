package com.exploreMate.content_service.service;

import com.exploreMate.content_service.dto.PageContentDto;
import com.exploreMate.content_service.model.PageContent;
import com.exploreMate.content_service.repository.PageContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final PageContentRepository repository;

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

    private PageContentDto mapToDto(PageContent page) {
        return PageContentDto.builder()
                .slug(page.getSlug())
                .title(page.getTitle())
                .contentBlocks(page.getContentBlocks())
                .status(page.getStatus())
                .updatedAt(page.getUpdatedAt() != null ? page.getUpdatedAt().toString() : null)
                .build();
    }
}
