package com.khangmoihocit.VocabFlow.modules.youtube_learning.repositories;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.entities.UserSegmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSegmentAttemptRepository extends JpaRepository<UserSegmentAttempt, Long> {
    List<UserSegmentAttempt> findByUserIdAndSegmentIdIn(UUID userId, List<Long> segmentIds);
}
