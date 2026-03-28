package com.khangmoihocit.VocabFlow.modules.youtube_learning.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @ToString.Exclude
    private VideoLesson video;

    @Column(name = "segment_order", nullable = false)
    private Integer segmentOrder;

    @Column(name = "start_time", nullable = false)
    private Double startTime;

    @Column(name = "end_time", nullable = false)
    private Double endTime;

    @Column(name = "english_text", columnDefinition = "TEXT", nullable = false)
    private String englishText;

    @Column(name = "vietnamese_translation", columnDefinition = "TEXT")
    private String vietnameseTranslation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
