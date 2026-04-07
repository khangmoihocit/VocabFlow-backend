package com.khangmoihocit.VocabFlow.modules.youtube_learning.services;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.AttemptRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.response.UserSegmentAttemptResponse;

import java.util.List;

public interface UserSegmentAttemptService {
    List<UserSegmentAttemptResponse> save(List<AttemptRequest> requests);
}
