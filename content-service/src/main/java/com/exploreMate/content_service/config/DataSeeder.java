package com.exploreMate.content_service.config;

import com.exploreMate.content_service.model.PageContent;
import com.exploreMate.content_service.repository.PageContentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initContent(PageContentRepository repository) {
        return args -> {
          try {
            if (repository.count() == 0) {
                // 1. Seed Blog Posts
                seedBlogPost(repository, 
                    "ultimate-packing-list", 
                    "The Ultimate Packing List for Southeast Asia", 
                    "Traveling light doesn't mean leaving essentials behind. Here's exactly what you need for a month-long trip through Thailand, Vietnam, and Cambodia.",
                    "https://images.unsplash.com/photo-1552422554-0d5af0c79fc6?auto=format&fit=crop&q=80&w=800",
                    "Guides", "8 min read", "Oct 12, 2023");

                seedBlogPost(repository, 
                    "ai-revolutionizing-travel", 
                    "How AI is Revolutionizing Solo Travel", 
                    "From real-time translation to safety monitoring, discover how artificial intelligence is making solo adventures safer and more accessible than ever before.",
                    "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=800",
                    "Tech", "6 min read", "Sep 28, 2023");

                seedBlogPost(repository, 
                    "eating-through-osaka", 
                    "Eating Your Way Through Osaka: A Foodie's Guide", 
                    "Forget Tokyo; Osaka is Japan's true kitchen. We explore the best street food stalls, hidden izakayas, and Michelin-starred spots you can't miss.",
                    "https://images.unsplash.com/photo-1574484284008-81dcec28d3e7?auto=format&fit=crop&q=80&w=800",
                    "Food", "12 min read", "Sep 15, 2023");

                seedBlogPost(repository, 
                    "sustainable-tourism-2024", 
                    "Sustainable Tourism: Traveling Responsibly in 2024", 
                    "As travelers, we have a responsibility to protect the places we visit. Learn practical tips for reducing your carbon footprint and supporting local communities.",
                    "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?auto=format&fit=crop&q=80&w=800",
                    "Eco", "10 min read", "Aug 30, 2023");

                System.out.println("Seeded initial content in Content Service");
            }
          } catch (Exception e) {
                System.out.println("WARNING: Could not seed content data (MongoDB may still be connecting): " + e.getMessage());
          }
        };
    }

    private void seedBlogPost(PageContentRepository repository, String slug, String title, String excerpt, String image, String category, String readTime, String date) {
        PageContent post = new PageContent();
        post.setSlug(slug);
        post.setTitle(title);
        post.setStatus("Published");
        post.setUpdatedAt(Instant.now());
        
        // Using contentBlocks as a flexible storage for meta info
        java.util.Map<String, String> blocks = new java.util.HashMap<>();
        blocks.put("excerpt", excerpt);
        blocks.put("image", image);
        blocks.put("category", category);
        blocks.put("readTime", readTime);
        blocks.put("date", date);
        blocks.put("content", "Full article content for " + title + " would go here...");
        
        post.setContentBlocks(blocks);
        
        repository.save(post);
    }
}
