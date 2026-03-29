package com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.YoutubeChannel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoLessonRequest {
    @NotNull(message = "channel id is require")
    Long youtubeChannelId;

    @NotBlank(message = "youtube video id là bắt buộc")
    String youtubeVideoId;

    @NotBlank(message = "tiêu đề video không được để trống")
    String title;

    String thumbnailUrl;

    String difficultyLevel;
    Boolean isPublished;
}
