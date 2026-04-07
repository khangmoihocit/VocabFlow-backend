package com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.VideoSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VideoSegmentRepository extends JpaRepository<VideoSegment, Long> {

    @Query("select vs from VideoSegment vs where vs.video.id = :videoId order by vs.segmentOrder asc")
    List<VideoSegment> findByVideoLessonId(Long videoId);

    @Transactional
    void deleteByVideoId(Long id);
}
