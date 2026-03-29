package com.khangmoihocit.VocabFlow.modules.youtube_learning.controllers;

import com.khangmoihocit.VocabFlow.core.dtos.ApiResponse;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.dtos.request.VideoSegmentToolRequest;
import com.khangmoihocit.VocabFlow.modules.youtube_learning.services.VideoSegmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("${spring.api.prefix}/video-segments")
public class VideoSegmentController {
    VideoSegmentService videoSegmentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{videoId}/import")
    ResponseEntity<?> importFromTool(@PathVariable Long videoId,
                                     @RequestBody List<VideoSegmentToolRequest> toolRequests){
        videoSegmentService.importSegmentsFromTool(videoId, toolRequests);
        return ResponseEntity.ok(
                ApiResponse.success("import thành công các segment của video " + videoId + " !"));
    }
}
