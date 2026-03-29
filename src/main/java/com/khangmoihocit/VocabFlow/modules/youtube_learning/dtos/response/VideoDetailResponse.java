package com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoLesson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoDetailResponse {
    VideoLessonSegmentResponse videoDetail;
    List<VideoSegmentResponse> segments;
}
