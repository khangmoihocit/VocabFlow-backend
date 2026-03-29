package com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.YoutubeChannel;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoLessonResponse {
    Long id;
    String youtubeVideoId;
    String title;
    String thumbnailUrl;
    String difficultyLevel;
    Boolean isPublished;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}