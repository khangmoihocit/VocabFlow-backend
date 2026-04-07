package com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.UserSegmentAttemptResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.UserSegmentAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserSegmentAttemptMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "segment.id", target = "videoSegmentId")
    UserSegmentAttemptResponse toResponse(UserSegmentAttempt entity);
}
