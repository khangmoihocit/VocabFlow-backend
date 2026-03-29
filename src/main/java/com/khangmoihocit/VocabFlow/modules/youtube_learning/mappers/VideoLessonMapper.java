package com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoLessonRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.VideoLessonResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoLesson;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoLessonMapper {
    VideoLesson toEntity(VideoLessonRequest request);

    VideoLessonResponse toResponse(VideoLesson entity);

    List<VideoLessonResponse> toListResponse(List<VideoLesson> entities);
}
