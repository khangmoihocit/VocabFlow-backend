package com.khangmoihocit.VocabFlow.modules.youtube_learning.services.Impl;

import com.khangmoihocit.VocabFlow.core.dtos.PageResponse;
import com.khangmoihocit.VocabFlow.core.enums.ErrorCode;
import com.khangmoihocit.VocabFlow.core.exception.AppException;
import com.khangmoihocit.VocabFlow.core.mapper.PageMapper;
import com.khangmoihocit.VocabFlow.core.specification.GenericSpecificationBuilder;
import com.khangmoihocit.VocabFlow.core.utils.SortUtil;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoLessonRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.VideoLessonResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoLesson;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.YoutubeChannel;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.mappers.VideoLessonMapper;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.VideoLessonRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories.YoutubeChannelRepository;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.VideoLessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j(topic = "Video Lesson SERVICE")
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoLessonServiceImpl implements VideoLessonService {

    VideoLessonMapper videoLessonMapper;
    PageMapper pageMapper;
    VideoLessonRepository videoLessonRepository;
    YoutubeChannelRepository youtubeChannelRepository;

    @Override
    public VideoLessonResponse createVideoLesson(VideoLessonRequest request) {
        YoutubeChannel channel = youtubeChannelRepository.findById(request.getYoutubeChannelId())
                .orElseThrow(() -> new AppException(ErrorCode.CHANNEL_NOT_FOUND));

        VideoLesson videoLesson = videoLessonMapper.toEntity(request);
        videoLesson.setChannel(channel);
        videoLesson = videoLessonRepository.save(videoLesson);
        return videoLessonMapper.toResponse(videoLesson);
    }

    @Override
    public PageResponse<VideoLessonResponse> getAllVideoLessons(int pageNo, int pageSize, String sort, Long channelId, String keyword) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, SortUtil.createSort(sort));

        GenericSpecificationBuilder<VideoLesson> builder = new GenericSpecificationBuilder<>();
        builder.withJoinById("channel", channelId);
        builder.with("isPublished", "=", true);

        if (StringUtils.hasText(keyword)) builder.with("title", ":", keyword);

        Specification<VideoLesson> specification = builder.build();
        Page<VideoLesson> videoLessonPage = videoLessonRepository.findAll(specification, pageable);

        return pageMapper.toPageResponse(videoLessonPage,
                videoLessonMapper.toListResponse(videoLessonPage.getContent()));
    }

    @Override
    public PageResponse<VideoLessonResponse> getAllVideoLessonsAdmin(int pageNo, int pageSize, String sort, Long channelId, String keyword) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, SortUtil.createSort(sort));

        GenericSpecificationBuilder<VideoLesson> builder = new GenericSpecificationBuilder<>();
        builder.withJoinById("channel", channelId);

        if (StringUtils.hasText(keyword)) builder.with("title", ":", keyword);

        Specification<VideoLesson> specification = builder.build();
        Page<VideoLesson> videoLessonPage = videoLessonRepository.findAll(specification, pageable);

        return pageMapper.toPageResponse(videoLessonPage,
                videoLessonMapper.toListResponse(videoLessonPage.getContent()));
    }

    @Override
    public VideoLessonResponse getVideoLessonById(Long id) {
        VideoLesson videoLesson = videoLessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));
        return videoLessonMapper.toResponse(videoLesson);
    }

    @Override
    public VideoLessonResponse updateVideoLesson(Long id, VideoLessonRequest request) {
        VideoLesson videoLesson = videoLessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        if (request.getYoutubeChannelId() != null && !request.getYoutubeChannelId().equals(videoLesson.getChannel().getId())) {
            YoutubeChannel newChannel = youtubeChannelRepository.findById(request.getYoutubeChannelId())
                    .orElseThrow(() -> new AppException(ErrorCode.CHANNEL_NOT_FOUND));
            videoLesson.setChannel(newChannel);
        }

        videoLesson.setTitle(request.getTitle());
        videoLesson.setYoutubeVideoId(request.getYoutubeVideoId());
        videoLesson.setThumbnailUrl(request.getThumbnailUrl());
        videoLesson.setDifficultyLevel(request.getDifficultyLevel());
        videoLesson.setIsPublished(request.getIsPublished());
        videoLesson.setDuration(request.getDuration());
        videoLesson.setViews(request.getViews());

        videoLesson = videoLessonRepository.save(videoLesson);
        return videoLessonMapper.toResponse(videoLesson);
    }

    @Override
    public void deleteVideoLesson(Long id) {
        VideoLesson videoLesson = videoLessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_LESSON_NOT_FOUND));

        videoLessonRepository.delete(videoLesson);
    }
}