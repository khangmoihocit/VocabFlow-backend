package com.khangmoihocit.VocabFlow.modules.youtube_learning.services;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoLessonRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.VideoLessonResponse;

public interface VideoLessonService {
    VideoLessonResponse createVideoLesson(VideoLessonRequest request);

    PageResponse<VideoLessonResponse> getAllVideoLessons(int pageNo, int pageSize, String sort, Long channelId, String keyword);

    PageResponse<VideoLessonResponse> getAllVideoLessonsAdmin(int pageNo, int pageSize, String sort, Long channelId, String keyword);

    VideoLessonResponse getVideoLessonById(Long id);

    VideoLessonResponse updateVideoLesson(Long id, VideoLessonRequest request);

    void deleteVideoLesson(Long id);
}
