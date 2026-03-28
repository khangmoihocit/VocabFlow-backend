package com.khangmoihocit.VocabFlow.modules.youtube_learning.entities;

import com.khangmoihocit.VocabFlow.modules.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_segment_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSegmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false)
    @ToString.Exclude
    private VideoSegment segment;

    @Column(name = "dictation_user_text", columnDefinition = "TEXT")
    private String dictationUserText;

    @Column(name = "dictation_score")
    private Integer dictationScore = 0;

    @Column(name = "shadowing_score")
    private Integer shadowingScore = 0;

    @Column(name = "is_mastered")
    private Boolean isMastered = false;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}