package com.khangmoihocit.VocabFlow.modules.youtube_learning.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @ToString.Exclude // Tránh lỗi lặp vô hạn khi log
    private YoutubeChannel channel;

    @Column(name = "youtube_video_id", nullable = false, unique = true)
    private String youtubeVideoId;

    @Column(nullable = false)
    private String title;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "difficulty_level", length = 20)
    @Builder.Default
    private String difficultyLevel = "MEDIUM";

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}