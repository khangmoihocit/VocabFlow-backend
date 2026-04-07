package com.khangmoihocit.VocabFlow.modules.youtube_learning.services.Impl;

import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.utils.UserDetailUtil;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoSegmentToolRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.*;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.UserSegmentAttempt;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoLesson;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoSegment;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers.VideoLessonMapper;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers.VideoSegmentMapper;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.UserSegmentAttemptRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoLessonRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoSegmentRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.VideoSegmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j(topic = "Video segment SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoSegmentServiceImpl implements VideoSegmentService {
    VideoLessonRepository videoLessonRepository;
    VideoSegmentRepository videoSegmentRepository;
    UserSegmentAttemptRepository userSegmentAttemptRepository;
    VideoSegmentMapper videoSegmentMapper;

    @Override
    public void insertSegment(Long videoId, List<VideoSegmentToolRequest> toolRequests) {
        VideoLesson videoLesson = videoLessonRepository.findById(videoId)
                .orElseThrow(()->new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        List<VideoSegment> segmentsToSave = toolRequests.stream()
                .map(req -> VideoSegment.builder()
                        .video(videoLesson)
                        .segmentOrder(req.getId())
                        .startTime(req.getStart())
                        .endTime(req.getEnd())
                        .englishText(req.getText())
                        .vietnameseTranslation(req.getVietnameseTranslation())
                        .ipa(req.getIpa())
                        .build()
                ).toList();

        videoSegmentRepository.saveAll(segmentsToSave);
    }

    @Transactional
    @Override
    public void updateSegment(Long videoId, List<VideoSegmentToolRequest> toolRequests) {
        VideoLesson videoLesson = videoLessonRepository.findById(videoId)
                .orElseThrow(()->new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        videoSegmentRepository.deleteByVideoId(videoLesson.getId());
        List<VideoSegment> segmentsToSave = toolRequests.stream()
                .map(req -> VideoSegment.builder()
                        .video(videoLesson)
                        .segmentOrder(req.getId())
                        .startTime(req.getStart())
                        .endTime(req.getEnd())
                        .englishText(req.getText())
                        .vietnameseTranslation(req.getVietnameseTranslation())
                        .ipa(req.getIpa())
                        .build()
                ).toList();

        videoSegmentRepository.saveAll(segmentsToSave);
    }

    @Override
    public VideoDetailResponse getById(Long videoId) {
        VideoLesson videoLesson = videoLessonRepository.findById(videoId)
                .orElseThrow(()->new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        List<VideoSegment> videoSegments = videoSegmentRepository.findByVideoLessonId(videoId);

        List<Long> segmentIds = videoSegments.stream()
                .map(VideoSegment::getId)
                .toList();
        List<UserSegmentAttempt> userAttempts = userSegmentAttemptRepository
                .findByUserIdAndSegmentIdIn(UserDetailUtil.get().getId(), segmentIds);
        Map<Long, UserSegmentAttempt> attemptMap = userAttempts.stream()
                .collect(Collectors.toMap(attempt -> attempt.getSegment().getId(),
                        Function.identity()));

        VideoLessonSegmentResponse videoLessonResponse = VideoLessonSegmentResponse.builder()
                .id(videoLesson.getId())
                .youtubeVideoId(videoLesson.getYoutubeVideoId())
                .title(videoLesson.getTitle())
                .channelName(videoLesson.getChannel().getName())
                .build();

        List<VideoSegmentResponse> videoSegmentResponses = videoSegments.stream().map(segment -> {
            VideoSegmentResponse response = videoSegmentMapper.toResponse(segment);

            // Kiểm tra xem user đã học câu này chưa
            UserSegmentAttempt attempt = attemptMap.get(segment.getId());
            if (attempt != null) {
                response.setUserAttempt(UserAttemptResponse.builder()
                        .dictationUserText(attempt.getDictationUserText())
                        .dictationScore(attempt.getDictationScore())
                        .shadowingScore(attempt.getShadowingScore())
                        .isMastered(attempt.getIsMastered())
                        .build());
            }
            return response;
        }).toList();

        return VideoDetailResponse.builder()
                .videoDetail(videoLessonResponse)
                .segments(videoSegmentResponses)
                .build();
    }
}
