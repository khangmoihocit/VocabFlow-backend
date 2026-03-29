package com.khangmoihocit.VocabFlow.modules.youtube_learning.services.Impl;

import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoSegmentToolRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoLesson;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoSegment;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoLessonRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoSegmentRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.VideoSegmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "Video segment SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoSegmentServiceImpl implements VideoSegmentService {
    VideoLessonRepository videoLessonRepository;
    VideoSegmentRepository videoSegmentRepository;

    @Override
    public void importSegmentsFromTool(Long videoId, List<VideoSegmentToolRequest> toolRequests) {
        VideoLesson videoLesson = videoLessonRepository.findById(videoId)
                .orElseThrow(()->new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        List<VideoSegment> segmentsToSave = toolRequests.stream()
                .map(req -> VideoSegment.builder()
                        .video(videoLesson)
                        .segmentOrder(req.getId())
                        .startTime(req.getStart())
                        .endTime(req.getEnd())
                        .englishText(req.getText())
                        .build()
                ).toList();

        videoSegmentRepository.saveAll(segmentsToSave);
    }
}
