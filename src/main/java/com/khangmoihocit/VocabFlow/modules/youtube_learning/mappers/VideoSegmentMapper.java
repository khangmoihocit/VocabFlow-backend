package com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.VideoSegmentResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoSegment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoSegmentMapper {
    VideoSegmentResponse toResponse(VideoSegment videoSegment);

    List<VideoSegmentResponse> toListResponse(List<VideoSegment> entities);
}
