package com.khangmoihocit.VocabFlow.modules.youtube_learning.services;

import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoSegmentToolRequest;

import java.util.List;

public interface VideoSegmentService {
    void importSegmentsFromTool(Long videoId, List<VideoSegmentToolRequest> toolRequests);
}
